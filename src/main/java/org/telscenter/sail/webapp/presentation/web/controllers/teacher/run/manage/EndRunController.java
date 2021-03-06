/**
 * Copyright (c) 2007 Regents of the University of California (Regents). Created
 * by TELS, Graduate School of Education, University of California at Berkeley.
 *
 * This software is distributed under the GNU Lesser General Public License, v2.
 *
 * Permission is hereby granted, without written agreement and without license
 * or royalty fees, to use, copy, modify, and distribute this software and its
 * documentation for any purpose, provided that the above copyright notice and
 * the following two paragraphs appear in all copies of this software.
 *
 * REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE SOFTWAREAND ACCOMPANYING DOCUMENTATION, IF ANY, PROVIDED
 * HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.telscenter.sail.webapp.presentation.web.controllers.teacher.run.manage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.sail.webapp.dao.ObjectNotFoundException;
import net.sf.sail.webapp.domain.User;
import net.sf.sail.webapp.presentation.web.controllers.ControllerUtil;
import net.sf.sail.webapp.service.AclService;
import net.sf.sail.webapp.service.NotAuthorizedException;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.telscenter.sail.webapp.domain.Run;
import org.telscenter.sail.webapp.domain.impl.EndRunParameters;
import org.telscenter.sail.webapp.service.authentication.UserDetailsService;
import org.telscenter.sail.webapp.service.offering.RunService;

/**
 * The controller for Ending a Run for teachers
 *
 * @author Hiroki Terashima
 * @version $Id$
 */
public class EndRunController extends SimpleFormController {

	private RunService runService;
	
	private AclService<Run> aclService;

	private static final String RUNID_PARAM_NAME = "runId";

	private static final String RUN_NAME_PARAM_NAME = "runName";
	
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		Run run = this.runService.retrieveById(Long.parseLong(request.getParameter(RUNID_PARAM_NAME)));
		User user = ControllerUtil.getSignedInUser();

		if(this.aclService.hasPermission(run, BasePermission.ADMINISTRATION, user)){
			EndRunParameters params = new EndRunParameters();
			params.setRunId(Long.parseLong(request.getParameter(RUNID_PARAM_NAME)));
			params.setRunName(request.getParameter(RUN_NAME_PARAM_NAME));
			return params;
		} else {
			throw new NotAuthorizedException("You do not have permission to archive this run.");
		}
	}
	
	/**
     * On submission of the End Run form, the selected run is ended,
     * provided that the logged-in user has the required privileges.
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object,
     *      org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors) {
    	EndRunParameters params = (EndRunParameters) command;
    	Long runId = params.getRunId();
		User user = ControllerUtil.getSignedInUser();

    	ModelAndView modelAndView = null;
    	Run run = null;
    	try {
			run = runService.retrieveById(runId);
			if (user.getUserDetails().hasGrantedAuthority(UserDetailsService.ADMIN_ROLE) || run.getOwners().contains(user)) {
				runService.endRun(run);
	        	modelAndView = new ModelAndView(getSuccessView());
			} else {
				errors.rejectValue("runId", "error.not-owner-of-run");	
				modelAndView = new ModelAndView(getFormView());
			}
		} catch (ObjectNotFoundException e) {
			errors.rejectValue("runId", "error.illegal-runId");
			modelAndView = new ModelAndView(getFormView());
		}
    	return modelAndView;
    }
	/**
	 * @param runService the runService to set
	 */
	public void setRunService(RunService runService) {
		this.runService = runService;
	}

	/**
	 * @param aclService the aclService to set
	 */
	public void setAclService(AclService<Run> aclService) {
		this.aclService = aclService;
	}
}
