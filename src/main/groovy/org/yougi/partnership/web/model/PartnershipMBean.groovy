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
package org.yougi.partnership.web.model

import org.primefaces.event.FileUploadEvent
import org.primefaces.model.DefaultStreamedContent
import org.primefaces.model.StreamedContent
import org.primefaces.model.UploadedFile
import org.yougi.business.ApplicationPropertyBean
import org.yougi.business.UserAccountBean
import org.yougi.entity.*
import org.yougi.partnership.business.PartnerBean
import org.yougi.partnership.business.RepresentativeBean
import org.yougi.partnership.entity.Partner
import org.yougi.partnership.entity.Representative
import org.yougi.reference.Properties
import org.yougi.util.WebTextUtils
import org.yougi.annotation.UserName
import org.yougi.web.model.LocationMBean

import javax.annotation.PostConstruct
import javax.ejb.EJB
import javax.enterprise.context.RequestScoped
import javax.faces.application.FacesMessage
import javax.faces.context.FacesContext
import javax.inject.Inject
import javax.inject.Named
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
class PartnershipMBean {

  private static final Logger LOGGER = Logger.getLogger(PartnershipMBean.class.getSimpleName())

  @EJB
  private RepresentativeBean representativeBean
  @EJB
  private UserAccountBean userAccountBean
  @EJB
  private PartnerBean partnerBean
  @EJB
  private ApplicationPropertyBean applicationPropertyBean
  @Inject
  LocationMBean locationMBean

  @Inject
  @UserName
  private String username

  @Inject
  private FacesContext context
  private Representative representative
  private StreamedContent logoImage

  String getFormattedPartnerDescription() {
    if (representative) {
      String description = representative.partner.description
      return WebTextUtils.convertLineBreakToHTMLParagraph(description)
    }
    null
  }

  boolean getRepresentativeExists() {
    representative.id
  }

  @PostConstruct
  void load() {
    UserAccount person = userAccountBean.findByUsername(username)
    this.representative = representativeBean.findRepresentative(person)

    if (representative == null) {
      representative = new Representative()
      representative.setPerson(person)
      Partner newPartner = new Partner()
      representative.setPartner(newPartner)
    } else if (!locationMBean.isInitialized()) {
      locationMBean.initialize()

      if (representative.partner.address.country) {
        locationMBean.setSelectedCountry(representative.partner.address.country.acronym)
      }

      if (representative.partner.address.province) {
        locationMBean.setSelectedProvince(representative.partner.address.province.id)
      }

      if (representative.partner.address.city) {
        locationMBean.setSelectedCity(representative.partner.address.city.id)
      }
    }

    loadLogoImage()
  }

  String save() {
    Country country = this.locationMBean.getCountry()
    if (country) {
      this.representative.getPartner().getAddress().setCountry(country)
    }

    Province province = this.locationMBean.getProvince()
    if (province != null) {
      this.representative.getPartner().getAddress().setProvince(province)
    }

    City city = this.locationMBean.getCity()
      if (city != null) {
        this.representative.getPartner().getAddress().setCity(city)
      }

    partnerBean.save(this.representative.getPartner())
      representativeBean.save(this.representative)

      context.getExternalContext().getSessionMap().remove('locationBean')

      return 'profile?faces-redirect=true&tab=2'
  }

  String remove() {
    representativeBean.remove(representative.getId())
    'profile?faces-redirect=true'
  }

  private void loadLogoImage() {
    try {
      String logoPath = this.representative.getPartner().getLogo()

        if (logoPath != null) {
          InputStream is = new FileInputStream(new File(logoPath))
            String[] logoPathArray = [logoPath] as String[]
            LOGGER.log(Level.INFO, 'JUG-0002: Loading logo file {0}', logoPathArray)
            logoImage = new DefaultStreamedContent(is, 'image/jpeg')
        }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e)
    }
  }

  void handleLogoFileUpload(FileUploadEvent event) {
    UploadedFile uploadedFile = event.getFile()
    String[] logParams = [uploadedFile.getFileName(), uploadedFile.getContentType(), System.getProperty('java.io.tmpdir')] as String[]
    LOGGER.log(Level.INFO, 'JUG-0001: File {0} of type {1} temporarely uploaded to {2}', logParams)
    try {
      /* Loads the representative related to the logged user. */
      UserAccount person = userAccountBean.findByUsername(username)
      this.representative = representativeBean.findRepresentative(person)

      /* Write the inputStream to a FileOutputStream */
      InputStream is = uploadedFile.getInputstream()
      ApplicationProperty applicationProperty = applicationPropertyBean.findApplicationProperty(Properties.FILE_REPOSITORY_PATH)
      String fileExtension = uploadedFile.getFileName()
      fileExtension = fileExtension.substring(fileExtension.indexOf('.'))
      StringBuilder filePath = new StringBuilder()
      filePath.append(applicationProperty.getPropertyValue())
      filePath.append('/')
      filePath.append(this.representative.getPartner().getId())
      filePath.append(fileExtension)
      OutputStream out = new FileOutputStream(new File(filePath.toString()))
      int read
      byte[] bytes = new byte[1024]

      while ((read = is.read(bytes)) != -1) {
        out.write(bytes, 0, read)
      }
      is.close()
      out.flush()
      out.close()

      /* If nothing goes wrong while saving the file,
      * then updates the database with the file location. */
      this.representative.getPartner().setLogo(filePath.toString())
      partnerBean.save(this.representative.getPartner())
      loadLogoImage()
    } catch (IOException ioe) {
      LOGGER.log(Level.INFO, ioe.getMessage(), ioe)
    } catch (Exception e) {
      LOGGER.log(Level.INFO, e.getMessage(), e)
    }
    FacesMessage msg = new FacesMessage('Succesful', uploadedFile.getSize() + ' bytes of the file ' + uploadedFile.getFileName() + ' are uploaded.')
    context.addMessage(null, msg)
  }

  String removeLogoImage() {
    try {
      String logoPath = this.representative.getPartner().getLogo()
      if (logoPath) {
        File logo = new File(logoPath)
        logo.delete()
        InputStream is = new FileInputStream(new File(logoPath))
        String[] logoPathArray = [logoPath] as String[]
        LOGGER.log(Level.INFO, 'JUG-0002: Loading logo file {0}', logoPathArray)
        logoImage = new DefaultStreamedContent(is, 'image/jpeg')
      }
    } catch (e) {
      LOGGER.log(Level.SEVERE, e.message, e)
    }
    'profile?faces-redirect=true&tab=2'
  }
}
