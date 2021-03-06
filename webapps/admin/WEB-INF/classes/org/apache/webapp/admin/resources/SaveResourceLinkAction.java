/*
 * $Header: /home/cvs/jakarta-tomcat-4.0/webapps/admin/WEB-INF/classes/org/apache/webapp/admin/resources/SaveResourceLinkAction.java,v 1.6 2002/11/08 11:48:46 remm Exp $
 * $Revision: 1.6 $
 * $Date: 2002/11/08 11:48:46 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Struts", and "Apache Software
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


package org.apache.webapp.admin.resources;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanInfo;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;


/**
 * <p>Implementation of <strong>Action</strong> that saves a new or
 * updated resource link entry.</p>
 *
 * @author Amy Roh
 * @version $Revision: 1.6 $ $Date: 2002/11/08 11:48:46 $
 * @since 4.1
 */

public final class SaveResourceLinkAction extends Action {


    // ----------------------------------------------------- Instance Variables

    /**
     * The MessageResources we will be retrieving messages from.
     */
    private MessageResources resources = null;


    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mserver = null;

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

        // Look up the components we will be using as needed
        if (mserver == null) {
            mserver = ((ApplicationServlet) getServlet()).getServer();
        }
        if (resources == null) {
            resources = getServlet().getResources();
        }
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Action.LOCALE_KEY);

        // Has this transaction been cancelled?
        if (isCancelled(request)) {
            return (mapping.findForward("List ResourceLinks Setup"));
        }

        // Check the transaction token
        if (!isTokenValid(request)) {
            response.sendError
                (HttpServletResponse.SC_BAD_REQUEST,
                 resources.getMessage(locale, "users.error.token"));
            return (null);
        }

        // Perform any extra validation that is required
        ResourceLinkForm resourceLinkForm = (ResourceLinkForm) form;
        String objectName = resourceLinkForm.getObjectName();
     
        // Perform an "Add ResourceLink" transaction
        if (objectName == null) {

            String signature[] = new String[3];
            signature[0] = "java.lang.String";
            signature[1] = "java.lang.String";
            signature[2] = "java.lang.String";

            Object params[] = new Object[3];
            params[0] = resourceLinkForm.getName();
            params[1] = resourceLinkForm.getType();   
            params[2] = resourceLinkForm.getGlobal();
            
            String resourcetype = resourceLinkForm.getResourcetype();
            String path = resourceLinkForm.getPath();
            String host = resourceLinkForm.getHost();
            String service = resourceLinkForm.getService();
                       
            ObjectName oname = null;

            try {

                if (resourcetype!=null) {
                    // Construct the MBean Name for the naming source
                    if (resourcetype.equals("Global")) {
                        oname = 
                            new ObjectName(ResourceUtils.NAMINGRESOURCES_TYPE +
                            ResourceUtils.GLOBAL_TYPE);
                    } else if (resourcetype.equals("Context")) {            
                        oname = 
                            new ObjectName (ResourceUtils.NAMINGRESOURCES_TYPE + 
                            ResourceUtils.CONTEXT_TYPE + ",path=" + path + 
                            ",host=" + host + ",service=" + service);
                    } else if (resourcetype.equals("DefaultContext")) {
                        if (host.length() > 0) {
                            oname = 
                                new ObjectName(ResourceUtils.NAMINGRESOURCES_TYPE +
                                ResourceUtils.HOST_DEFAULTCONTEXT_TYPE + ",host=" + 
                                host + ",service=" + service);
                        } else {
                            oname = 
                                new ObjectName(ResourceUtils.NAMINGRESOURCES_TYPE +
                                ResourceUtils.SERVICE_DEFAULTCONTEXT_TYPE + ",service=" + 
                                service);
                        }
                    }
                }
                // Create the new object and associated MBean
                objectName = (String) mserver.invoke(oname, "addResourceLink",
                                                     params, signature);
                                     
            } catch (Exception e) {

                getServlet().log
                    (resources.getMessage(locale, "users.error.invoke",
                                          "addResourceLink"), e);
                response.sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     resources.getMessage(locale, "users.error.invoke",
                                          "addResourceLink"));
                return (null);
            }

        }
        
        // Perform an "Update User database" transaction
        String attribute = null;
        try {
            
            ObjectName oname = new ObjectName(objectName);
            
            attribute = "global";
            mserver.setAttribute
                (oname,
                 new Attribute(attribute, resourceLinkForm.getGlobal()));
            attribute = "type";
            mserver.setAttribute
                (oname,
                 new Attribute(attribute, resourceLinkForm.getType()));

        } catch (Exception e) {

            getServlet().log
                (resources.getMessage(locale, "users.error.set.attribute",
                                      attribute), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.set.attribute",
                                      attribute));
            return (null);

        }
        
        // Proceed to the list entries screen
        return (mapping.findForward("ResourceLinks List Setup"));

    }


}
