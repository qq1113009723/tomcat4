/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/context/AddContextAction.java,v 1.5 2003/03/14 16:00:16 remm Exp $
 * $Revision: 1.5 $
 * $Date: 2003/03/14 16:00:16 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
 */

package org.apache.webapp.admin.context;

import java.io.IOException;
import java.util.Locale;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;
import org.apache.webapp.admin.TomcatTreeBuilder;
/**
 * The <code>Action</code> that sets up <em>Add Context</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision: 1.5 $ $Date: 2003/03/14 16:00:16 $
 */

public class AddContextAction extends Action {
    
    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;
    
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response)
    throws IOException, ServletException {
        
        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);
        if (resources == null) {
            resources = getServlet().getResources();
        }
        
        // Fill in the form values for display and editing
        ContextForm contextFm = new ContextForm();
        session.setAttribute("contextForm", contextFm);
        contextFm.setAdminAction("Create");
        contextFm.setObjectName("");
        String parent = request.getParameter("parent");
        contextFm.setParentObjectName(parent);
        int position = parent.indexOf(",");
        String loader = TomcatTreeBuilder.LOADER_TYPE + 
                parent.substring(position, parent.length());
        String manager = TomcatTreeBuilder.MANAGER_TYPE + 
                parent.substring(position, parent.length());
        contextFm.setLoaderObjectName(loader);
        contextFm.setManagerObjectName(manager); 
        contextFm.setNodeLabel("");
        contextFm.setCookies("");
        contextFm.setCrossContext("false");
        contextFm.setDocBase("");
        contextFm.setOverride("false");
        contextFm.setPath("");
        contextFm.setReloadable("false");
        contextFm.setSwallowOutput("false");
        contextFm.setUseNaming("false");
        contextFm.setWorkDir("");        
        contextFm.setPath("");
        contextFm.setDebugLvl("0");
        //loader initialization
        contextFm.setLdrCheckInterval("15");
        contextFm.setLdrDebugLvl("0");
        contextFm.setLdrReloadable("false");
        //manager initialization
        contextFm.setMgrCheckInterval("60");
        contextFm.setMgrDebugLvl("0");
        contextFm.setMgrMaxSessions("-1");
        contextFm.setMgrSessionIDInit("");
        
        contextFm.setDebugLvlVals(Lists.getDebugLevels());
        contextFm.setBooleanVals(Lists.getBooleanValues());        
        
        // Forward to the context display page
        return (mapping.findForward("Context"));
        
    }    
}
