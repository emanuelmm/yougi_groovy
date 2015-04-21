/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.web.model

import org.yougi.annotation.ManagedProperty
import org.yougi.annotation.UserName
import org.yougi.business.AuthenticationBean
import org.yougi.business.CommunityBean
import org.yougi.business.CommunityMemberBean
import org.yougi.business.MessageHistoryBean
import org.yougi.business.UserAccountBean
import org.yougi.business.UserSessionBean
import org.yougi.entity.Authentication
import org.yougi.entity.Community
import org.yougi.entity.CommunityMember
import org.yougi.entity.MessageHistory
import org.yougi.entity.UserAccount
import org.yougi.entity.UserSession
import org.yougi.reference.DeactivationType
import org.yougi.util.ResourceBundleHelper

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.component.UIComponent
import javax.faces.context.FacesContext
import javax.faces.validator.ValidatorException
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class UserAccountMBean implements Serializable {

  private static final Logger LOGGER = Logger.getLogger(UserAccountMBean.simpleName)

  @EJB
  UserAccountBean userAccountBean
  @EJB
  AuthenticationBean authenticationBean
  @EJB
  MessageHistoryBean messageHistoryBean
  @EJB
  UserSessionBean userSessionBean
  @EJB
  CommunityBean communityBean
  @EJB
  CommunityMemberBean communityMemberBean
  @Inject
  LocationMBean locationMBean
  @Inject
  @ManagedProperty('#{param.id}')
  String id
  @Inject
  @ManagedProperty('#{param.letter}')
  String letter
  @Inject
  FacesContext context
  @Inject
  HttpServletRequest request
  @Inject
  @UserName
  String username
  String userId
  UserAccount userAccount
  Authentication authentication
  String password
  String passwordConfirmation
  String validationEmail
  String email
  Boolean validationPrivacy = false
  Boolean hasMultipleCommunities
  List<UserAccount> userAccounts
  List<UserAccount> unverifiedUserAccounts
  List<UserAccount> unconfirmedUserAccounts
  List<UserAccount> deactivatedUserAccounts
  List<Community> existingCommunities
  List<MessageHistory> historicMessages
  List<UserSession> userSessions
  Map<String, Boolean> selectedCommunities

  Map<String, Boolean> getSelectedCommunities() {
    if(selectedCommunities == null) {
      selectedCommunities = [:]

      existingCommunities = getExistingCommunities()
      existingCommunities.each { Community c -> selectedCommunities[c.id] = false }
    }
    selectedCommunities
  }

  // Beginning of mail validation
  void validateEmail(FacesContext context, UIComponent component, value) {
    validationEmail = (String) value

    if(userAccountBean.existingAccount(validationEmail)) {
      throw newValidatorException('errorCode0004')
    }
  }

  void validateEmailConfirmation(FacesContext context, UIComponent component, value) {
    String validationEmailConfirmation = (String) value
    if(!validationEmailConfirmation.equals(validationEmail)) {
      throw newValidatorException('errorCode0003')
    }
  }
  // End of email validation

  // Beginning of password validation
  void validatePassword(FacesContext context, UIComponent component, value) {
    password = (String) value
  }

  void validatePasswordConfirmation(FacesContext context, UIComponent component, value) {
    passwordConfirmation = (String) value
    if(passwordConfirmation != password) {
      throw newValidatorException('errorCode0005')
    }
  }
  // End of password validation

  // Beginning of privacy composite validation
  void validatePrivacyOption(FacesContext context, UIComponent component, value) {
    if(!validationPrivacy) {
      validationPrivacy = (Boolean) value
    }
  }

  void validatePrivacy(FacesContext context, UIComponent component, value) {
    if(!validationPrivacy) {
      validationPrivacy = (Boolean) value
      throw newValidatorException('errorCode0007')
    }
  }
  // End of privacy composite validation

  Boolean getNoAccount() {
    return userAccountBean.thereIsNoAccount()
  }

  boolean isConfirmed() {
    !userAccount.confirmationCode
  }

  void validateUserId(FacesContext context, UIComponent toValidate, value) {
    String usrId = (String) value
    if(!usrId.contains('@')) {
      throw new ValidatorException(new FacesMessage('Invalid email address.'))
    }
  }

  String getUserAccountByEmail() {
    UserAccount ua = userAccountBean.findByEmail(email)
    userAccounts = (List<UserAccount>)(ua ? [ua] : [])
    return 'users'
  }

  List<UserAccount> getUnverifiedUserAccounts() {
    if(unverifiedUserAccounts == null) {
      unverifiedUserAccounts = userAccountBean.findAllUnverifiedAccounts()
    }
    return unverifiedUserAccounts
  }

  List<UserAccount> getUnconfirmedUserAccounts() {
    if(unconfirmedUserAccounts == null) {
      unconfirmedUserAccounts = userAccountBean.findAllUnconfirmedAccounts()
    }
    return unconfirmedUserAccounts
  }

  List<UserAccount> getDeactivatedUserAccounts() {
    if(deactivatedUserAccounts == null) {
      deactivatedUserAccounts = userAccountBean.findAllDeactivatedUserAccounts()
    }
    return deactivatedUserAccounts
  }

  @PostConstruct
  void load() {
    if(id) {
      userAccount = userAccountBean.find(id)
      authentication = authenticationBean.findByUserAccount(userAccount)
      historicMessages = messageHistoryBean.findByRecipient(userAccount)
      userSessions = userSessionBean.findByUserAccount(userAccount)
    } else if(username != null) {
      userAccount = userAccountBean.findByUsername(username)
    } else {
      userAccount = new UserAccount()
    }

    locationMBean.selectedCountry = userAccount.country?.acronym
    locationMBean.selectedProvince = userAccount.province?.id
    locationMBean.selectedCity = userAccount.city?.id

    if(userAccount.timeZone) {
      locationMBean.selectedTimeZone = userAccount.timeZone
    }

    if(letter) {
      userAccounts = userAccountBean.findAllStartingWith(letter)
    }
  }

  String register() {
    boolean isFirstUser = userAccountBean.thereIsNoAccount()

    if(context.validationFailed) {
      return 'registration'
    }

    final UserAccount newUserAccount
    try {
      Authentication authentication = new Authentication(
        userAccount: userAccount,
        username: userAccount.unverifiedEmail,
        password: password)
      newUserAccount = userAccountBean.register(userAccount, authentication)
    } catch(e) {
      LOGGER.log(Level.INFO, e.message, e)
      context.addMessage(userId, new FacesMessage(e.cause.message))
      return 'registration'
    }

    if(!hasMultipleCommunities) {
      Community community = communityBean.findMainCommunity()
      if (community) {
        CommunityMember communityMember = new CommunityMember(community, newUserAccount)
        communityMemberBean.save(communityMember)
      }
    }

    if(isFirstUser) {
      addContextInfoMessage('infoSuccessfulRegistration')
      return 'login'
    }

    if(hasMultipleCommunities) {
      return 'registration_communities?id=' + userAccount.id
    }

    addContextInfoMessage('infoRegistrationConfirmationRequest')
    return 'registration_confirmation'
  }

  String registerToCommunities() {
    userAccount = userAccountBean.find(userAccount.id)

    selectedCommunities.each { String key, Boolean value ->
      if(value) {
        Community community = communityBean.find(key)
        CommunityMember communityMember = new CommunityMember(community, userAccount)
        communityMemberBean.save(communityMember)
      }
    }

    addContextInfoMessage('infoRegistrationConfirmationRequest')
    'registration_confirmation'
  }

  String save() {
    UserAccount existingUserAccount = userAccountBean.find(userAccount.id)

    if(existingUserAccount) {
      existingUserAccount.country = locationMBean.country
      existingUserAccount.province = locationMBean.province
      existingUserAccount.city = locationMBean.city
      existingUserAccount.firstName = userAccount.firstName
      existingUserAccount.lastName = userAccount.lastName
      existingUserAccount.gender = userAccount.gender
      existingUserAccount.website = userAccount.website
      existingUserAccount.twitter = userAccount.twitter
      userAccountBean.save(existingUserAccount)
    }
    else {
      userAccountBean.save(userAccount)
    }

    'users?faces-redirect=true'
  }

  String savePersonalData() {
    if (userAccount) {
      UserAccount existingUserAccount = userAccountBean.find(userAccount.id)

      existingUserAccount.country = locationMBean.country
      existingUserAccount.province = locationMBean.province
      existingUserAccount.city = locationMBean.city
      existingUserAccount.timeZone = locationMBean.selectedTimeZone
      existingUserAccount.firstName = userAccount.firstName
      existingUserAccount.lastName = userAccount.lastName
      existingUserAccount.gender = userAccount.gender
      existingUserAccount.website = userAccount.website
      existingUserAccount.twitter = userAccount.twitter
      existingUserAccount.publicProfile = userAccount.publicProfile
      userAccountBean.save(existingUserAccount)

      context.externalContext.sessionMap.remove('locationBean')
    }
    'profile?faces-redirect=true'
  }

  String savePrivacy() {
    if(userAccount) {
      UserAccount existingUserAccount = userAccountBean.find(userAccount.id)
      existingUserAccount.publicProfile = userAccount.publicProfile
      existingUserAccount.mailingList = userAccount.mailingList
      existingUserAccount.news = userAccount.news
      existingUserAccount.generalOffer = userAccount.generalOffer
      existingUserAccount.jobOffer = userAccount.jobOffer
      existingUserAccount.event = userAccount.event
      existingUserAccount.sponsor = userAccount.sponsor
      existingUserAccount.speaker = userAccount.speaker

      if(!isPrivacyValid(existingUserAccount)) {
        context.addMessage(null, new FacesMessage('Selecione pelo menos uma das opções de privacidade.'))
        return 'privacy'
      }

      userAccountBean.save(existingUserAccount)
    }
    'profile?faces-redirect=true'
  }

  String confirm() {
    try {
      userAccountBean.confirmUser(userAccount.confirmationCode)
    } catch (IllegalArgumentException iae) {
      LOGGER.log(Level.INFO, iae.message, iae)
      context.addMessage(null, new FacesMessage(iae.message))
      return 'user'
    }
    'users?faces-redirect=true'
  }

  String checkUserAsVerified() {
    userAccountBean.markUserAsVerified(userAccount)
    'user?faces-redirect=true'
  }

  String deactivateMembershipOwnWill() {
    userAccountBean.deactivateMembership(userAccount, DeactivationType.OWNWILL)

    HttpSession session = (HttpSession) context.externalContext.getSession(false)
    try {
      request.logout()
      session.invalidate()
    } catch(ServletException se) {
      LOGGER.log(Level.INFO, se.message, se)
    }

    '/index?faces-redirect=true'
  }

  String deactivateMembership() {
    userAccountBean.deactivateMembership(userAccount, DeactivationType.ADMINISTRATIVE)
    'users?faces-redirect=true'
  }

  /** Check whether at least one of the privacy options was checked. */
  private boolean isPrivacyValid(UserAccount userAccount) {
    userAccount.publicProfile ||
    userAccount.mailingList ||
    userAccount.event ||
    userAccount.news ||
    userAccount.generalOffer ||
    userAccount.jobOffer ||
    userAccount.sponsor ||
    userAccount.speaker
  }

  /**
   * Remove the current user permanently and navigate to the users view.
   * @return the next step in the navigation logic.
   */
  String removeUserAccount() {
    userAccountBean.remove(userAccount.id)
    'users?faces-redirect=true'
  }

  List<Community> getExistingCommunities() {
    if (!existingCommunities) {
      existingCommunities = communityBean.findAll()
    }
    existingCommunities
  }

  Boolean getHasMultipleCommunities() {
    if (!hasMultipleCommunities) {
      hasMultipleCommunities = communityBean.hasMultipleCommunities()
    }
    hasMultipleCommunities
  }

  private void addContextInfoMessage(String key) {
      context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.getMessage(key), ''))
  }

  private ValidatorException newValidatorException(String key) {
    new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.getMessage(key), null))
  }
}
