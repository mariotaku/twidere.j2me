/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package repackaged.com.sun.cldc.io;

import java.io.IOException;
import javax.microedition.io.Connection;


/**
 * Base class for Connection protocols.
 *
 * @author  Nik Shaylor
 * @version 1.1 2/21/2000
 */
public interface ConnectionBaseInterface {

    public Connection openPrim(String name, int mode, boolean timeouts)
        throws IOException;

}

