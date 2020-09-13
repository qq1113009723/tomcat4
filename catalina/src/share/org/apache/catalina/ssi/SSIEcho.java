/*
 * SSIEcho.java
 * $Header: /home/cvs/jakarta-tomcat-4.0/catalina/src/share/org/apache/catalina/ssi/SSIEcho.java,v 1.2 2002/11/25 10:15:42 dsandberg Exp $
 * $Revision: 1.2 $
 * $Date: 2002/11/25 10:15:42 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package org.apache.catalina.ssi;


import java.io.PrintWriter;

/**
 * Return the result associated with the supplied Server Variable.
 *
 * @author Bip Thelin
 * @author Paul Speed
 * @author Dan Sandberg
 * @version $Revision: 1.2 $, $Date: 2002/11/25 10:15:42 $
 */
public class SSIEcho implements SSICommand {
    protected final static String DEFAULT_ENCODING = "entity";
    protected final static String MISSING_VARIABLE_VALUE = "(none)";

    /**
     * @see SSICommand
     */
    public void process(SSIMediator ssiMediator,
			String commandName,
			String[] paramNames,
			String[] paramValues,
			PrintWriter writer) {

	String encoding = DEFAULT_ENCODING;
	String errorMessage = ssiMediator.getConfigErrMsg();

	for ( int i=0; i < paramNames.length; i++ ) {
	    String paramName = paramNames[i];
	    String paramValue = paramValues[i];

	    if ( paramName.equalsIgnoreCase("var") ) {
		String variableValue = ssiMediator.getVariableValue( paramValue, encoding );
		if ( variableValue == null ) {
		    variableValue = MISSING_VARIABLE_VALUE;
		}
		writer.write( variableValue );
	    } else if ( paramName.equalsIgnoreCase("encoding") ) {
		if ( isValidEncoding( paramValue ) ) {
		    encoding = paramValue;
		} else {
		    ssiMediator.log("#echo--Invalid encoding: " + paramValue );
		    writer.write( errorMessage );
		}
	    } else {
		ssiMediator.log("#echo--Invalid attribute: " + paramName );
		writer.write( errorMessage );
	    }
	}
    }

    protected boolean isValidEncoding( String encoding ) {
	return
	    encoding.equalsIgnoreCase("url") ||
	    encoding.equalsIgnoreCase("entity") ||
	    encoding.equalsIgnoreCase("none");
    }
}
