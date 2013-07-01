/*
 * GZIP library for j2me applications.
 *
 * Copyright (c) 2004-2006 Carlos Araiz (caraiz@java4ever.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.kalmeo.util;

import java.io.IOException;

/**
 * Clase que permite leer ficheros GZIP.
 * 
 * @author Carlos Araiz
 * @version 1.2.0
 */
public class GZIP {

	// Máscaras para el flag.
	private static final int FHCRC_MASK = 2;
	private static final int FEXTRA_MASK = 4;
	private static final int FNAME_MASK = 8;
	private static final int FCOMMENT_MASK = 16;

	// Tipos de bloques.
	private static final int BTYPE_NONE = 0;
	private static final int BTYPE_DYNAMIC = 2;

	// Límites.
	private static final int MAX_BITS = 16;
	private static final int MAX_CODE_LITERALS = 287;
	private static final int MAX_CODE_DISTANCES = 31;
	private static final int MAX_CODE_LENGTHS = 18;
	private static final int EOB_CODE = 256;

	// Datos prefijados (LENGTH: 257..287 / DISTANCE: 0..29 / DYNAMIC_LENGTH_ORDER: 0..18).
	private static final int LENGTH_EXTRA_BITS[] = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99 };
	private static final int LENGTH_VALUES[] = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0 };
	private static final int DISTANCE_EXTRA_BITS[] = { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
	private static final int DISTANCE_VALUES[] = { 1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577 };
	private static final int DYNAMIC_LENGTH_ORDER[] = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

	// Variables para la lectura de datos comprimidos.
	private static int gzipIndex, gzipByte, gzipBit;

	/**
	 * Descomprime un fichero GZIP.
	 * 
	 * @param gzip Array con los datos del fichero comprimido
	 * @return Array con los datos descomprimidos
	 */
	public static byte[] inflate(byte[] gzip) throws IOException {
		return inflate(gzip, 0, gzip.length);
	}
	
	/**
	 * Descomprime un fichero GZIP.
	 * 
	 * @param gzip Array con los datos del fichero comprimido
	 * @param offset
	 * @param length
	 * @return Array con los datos descomprimidos
	 */
	public static byte[] inflate(byte[] gzip, int offset, int length) throws IOException {
		// Inicializa.
		int gzipMaxIndex = Math.min(offset + length, gzip.length);
		gzipIndex = offset;
		gzipByte = gzipBit = 0;
		// Cabecera.
		if (readBits(gzip, 16) != 0x8B1F || readBits(gzip, 8) != 8) {
			throw new IOException("Invalid GZIP format");
		}
		// Flag.
		int flg = readBits(gzip, 8);
		// Fecha(4) / XFL(1) / OS(1).
		gzipIndex += 6;
		// Comprueba los flags.
		if ((flg & FEXTRA_MASK) != 0) {
			gzipIndex += readBits(gzip, 16);
		}
		if ((flg & FNAME_MASK) != 0) {
			while (gzip[gzipIndex++] != 0) {
			}
		}
		if ((flg & FCOMMENT_MASK) != 0) {
			while (gzip[gzipIndex++] != 0) {
			}
		}
		if ((flg & FHCRC_MASK) != 0) {
			gzipIndex += 2;
		}
		// Tamaño de los datos descomprimidos.
		int index = gzipIndex;
		gzipIndex = gzip.length - 4;
		byte uncompressed[] = new byte[readBits(gzip, 16) | (readBits(gzip, 16) << 16)];
		int uncompressedIndex = 0;
		gzipIndex = index;
		// Bloque con datos comprimidos.
		int bfinal = 0, btype = 0;
		do {
			// Lee la cabecera del bloque.
			bfinal = readBits(gzip, 1);
			btype = readBits(gzip, 2);
			// Comprueba el tipo de compresión.
			if (btype == BTYPE_NONE) {
				// Ignora los bits dentro del byte actual.
				gzipBit = 0;
				// LEN.
				int len = readBits(gzip, 16);
				// NLEN.
				readBits(gzip, 16);
				// Lee los datos.
				System.arraycopy(gzip, gzipIndex, uncompressed, uncompressedIndex, len);
				gzipIndex += len;
				// Actualiza el índice de los datos descomprimidos.
				uncompressedIndex += len;
			} else {
				int literalTree[], distanceTree[];
				if (btype == BTYPE_DYNAMIC) {
					// Número de datos de cada tipo.
					int hlit = readBits(gzip, 5) + 257;
					int hdist = readBits(gzip, 5) + 1;
					int hclen = readBits(gzip, 4) + 4;
					// Lee el número de bits para cada código de longitud.
					byte lengthBits[] = new byte[MAX_CODE_LENGTHS + 1];
					for (int i = 0; i < hclen; i++)
						lengthBits[DYNAMIC_LENGTH_ORDER[i]] = (byte) readBits(gzip, 3);
					// Crea los códigos para la longitud.
					int lengthTree[] = createHuffmanTree(lengthBits, MAX_CODE_LENGTHS);
					// Genera los árboles.
					literalTree = createHuffmanTree(decodeCodeLengths(gzip, lengthTree, hlit), hlit - 1);
					distanceTree = createHuffmanTree(decodeCodeLengths(gzip, lengthTree, hdist), hdist - 1);
				} else {
					byte literalBits[] = new byte[MAX_CODE_LITERALS + 1];
					for (int i = 0; i < 144; i++) {
						literalBits[i] = 8;
					}
					for (int i = 144; i < 256; i++) {
						literalBits[i] = 9;
					}
					for (int i = 256; i < 280; i++) {
						literalBits[i] = 7;
					}
					for (int i = 280; i < 288; i++) {
						literalBits[i] = 8;
					}
					literalTree = createHuffmanTree(literalBits, MAX_CODE_LITERALS);
					//
					byte distanceBits[] = new byte[MAX_CODE_DISTANCES + 1];
					for (int i = 0; i < distanceBits.length; i++) {
						distanceBits[i] = 5;
					}
					distanceTree = createHuffmanTree(distanceBits, MAX_CODE_DISTANCES);
				}
				// Descomprime el bloque.
				int code = 0, leb = 0, deb = 0;
				while ((code = readCode(gzip, literalTree)) != EOB_CODE) {
					if (code > EOB_CODE) {
						code -= 257;
						int len = LENGTH_VALUES[code];
						if ((leb = LENGTH_EXTRA_BITS[code]) > 0) {
							len += readBits(gzip, leb);
						}
						code = readCode(gzip, distanceTree);
						int distance = DISTANCE_VALUES[code];
						if ((deb = DISTANCE_EXTRA_BITS[code]) > 0) {
							distance += readBits(gzip, deb);
						}
						// Repite la información.
						int localOffset = uncompressedIndex - distance;
						while (distance < len) {
							System.arraycopy(uncompressed, localOffset, uncompressed, uncompressedIndex, distance);
							uncompressedIndex += distance;
							len -= distance;
							distance <<= 1;
						}
						System.arraycopy(uncompressed, localOffset, uncompressed, uncompressedIndex, len);
						uncompressedIndex += len;
					} else {
						uncompressed[uncompressedIndex++] = (byte) code;
					}
				}
			}
		} while (bfinal == 0 && gzipIndex < gzipMaxIndex);
		//
		return uncompressed;
	}

	/**
	 * Lee un número de bits
	 * 
	 * @param n Número de bits [0..16]
	 */
	private static int readBits(byte gzip[], int n) {
		// Asegura que tenemos un byte.
		int data = (gzipBit == 0 ? (gzipByte = (gzip[gzipIndex++] & 0xFF)) : (gzipByte >> gzipBit));
		// Lee hasta completar los bits.
		for (int i = (8 - gzipBit); i < n; i += 8) {
			gzipByte = (gzip[gzipIndex++] & 0xFF);
			data |= (gzipByte << i);
		}
		// Ajusta la posición actual.
		gzipBit = (gzipBit + n) & 7;
		// Devuelve el dato.
		return (data & ((1 << n) - 1));
	}

	/**
	 * Lee un código.
	 */
	private static int readCode(byte gzip[], int tree[]) {
		int node = tree[0];
		while (node >= 0) {
			// Lee un byte si es necesario.
			if (gzipBit == 0)
				gzipByte = (gzip[gzipIndex++] & 0xFF);
			// Accede al nodo correspondiente.
			node = (((gzipByte & (1 << gzipBit)) == 0) ? tree[node >> 16] : tree[node & 0xFFFF]);
			// Ajusta la posición actual.
			gzipBit = (gzipBit + 1) & 7;
		}
		return (node & 0xFFFF);
	}

	/**
	 * Decodifica la longitud de códigos (usado en bloques comprimidos con
	 * códigos dinámicos).
	 */
	private static byte[] decodeCodeLengths(byte gzip[], int lengthTree[], int count) {
		byte bits[] = new byte[count];
		for (int i = 0, code = 0, last = 0; i < count;) {
			code = readCode(gzip, lengthTree);
			if (code >= 16) {
				int repeat = 0;
				if (code == 16) {
					repeat = 3 + readBits(gzip, 2);
					code = last;
				} else {
					if (code == 17) {
						repeat = 3 + readBits(gzip, 3);
					} else {
						repeat = 11 + readBits(gzip, 7);
					}
					code = 0;
				}
				while (repeat-- > 0) {
					bits[i++] = (byte) code;
				}
			} else {
				bits[i++] = (byte) code;
			}
			//
			last = code;
		}
		return bits;
	}

	/**
	 * Crea el árbol para los códigos Huffman.
	 */
	private static int[] createHuffmanTree(byte bits[], int maxCode) {
		// Número de códigos por cada longitud de código.
		int bl_count[] = new int[MAX_BITS + 1];
		for (int i = 0; i < bits.length; i++)
			bl_count[bits[i]]++;
		// Mínimo valor numérico del código para cada longitud de código.
		int code = 0;
		bl_count[0] = 0;
		int next_code[] = new int[MAX_BITS + 1];
		for (int i = 1; i <= MAX_BITS; i++)
			next_code[i] = code = (code + bl_count[i - 1]) << 1;
		// Genera el árbol.
		// Bit 31 => Nodo (0) o código (1).
		// (Nodo) bit 16..30 => índice del nodo de la izquierda (0 si no tiene).
		// (Nodo) bit 0..15 => índice del nodo de la derecha (0 si no tiene).
		// (Código) bit 0..15
		int tree[] = new int[(maxCode << 1) + MAX_BITS];
		int treeInsert = 1;
		for (int i = 0; i <= maxCode; i++) {
			int len = bits[i];
			if (len != 0) {
				code = next_code[len]++;
				// Lo mete en en árbol.
				int node = 0;
				for (int bit = len - 1; bit >= 0; bit--) {
					int value = code & (1 << bit);
					// Inserta a la izquierda.
					if (value == 0) {
						int left = tree[node] >> 16;
						if (left == 0) {
							tree[node] |= (treeInsert << 16);
							node = treeInsert++;
						} else
							node = left;
					}
					// Inserta a la derecha.
					else {
						int right = tree[node] & 0xFFFF;
						if (right == 0) {
							tree[node] |= treeInsert;
							node = treeInsert++;
						} else
							node = right;
					}
				}
				// Inserta el código.
				tree[node] = 0x80000000 | i;
			}
		}
		return tree;
	}
}