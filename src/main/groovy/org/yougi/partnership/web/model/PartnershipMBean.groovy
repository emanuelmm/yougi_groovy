/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.yougi.partnership.web.model;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.yougi.business.ApplicationPropertyBean;
import org.yougi.business.UserAccountBean;
import org.yougi.entity.*;
import org.yougi.partnership.business.PartnerBean;
import org.yougi.partnership.business.RepresentativeBean;
import org.yougi.partnership.entity.Partner;
import org.yougi.partnership.entity.Representative;
import org.yougi.reference.Properties;
import org.yougi.util.WebTextUtils;
import org.yougi.annotation.UserName;
import org.yougi.web.model.LocationMBean;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@RequestScoped
public class PartnershipMBean {

    private static final Logger LOGGER = Logger.getLogger(PartnershipMBean.class.getSimpleName());

    @EJB
    private RepresentativeBean representativeBean;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private PartnerBean partnerBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @Inject
    private LocationMBean locationMBean;

    @Inject
    @UserName
    private String username;

    @Inject
    private FacesContext context;

    private Representative representative;

    private StreamedContent logoImage;

    public PartnershipMBean() {
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }

    public StreamedContent getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(StreamedContent logoImage) {
        this.logoImage = logoImage;
    }

    public String getFormattedPartnerDescription() {
        if (representative != null) {
            String description = this.representative.getPartner().getDescription();
            return WebTextUtils.convertLineBreakToHTMLParagraph(description);
        }
        return null;
    }

    public boolean getRepresentativeExists() {
        if (this.representative.getId() != null) {
            return true;
        }
        return false;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    @PostConstruct
    public void load() {
        UserAccount person = userAccountBean.findByUsername(username);
        this.representative = representativeBean.findRepresentative(person);

        if (this.representative == null) {
            this.representative = new Representative();
            this.representative.setPerson(person);
            Partner newPartner = new Partner();
            this.representative.setPartner(newPartner);
        } else if (!locationMBean.isInitialized()) {
            locationMBean.initialize();

            if (this.representative.getPartner().getAddress().getCountry() != null) {
                locationMBean.setSelectedCountry(this.representative.getPartner().getAddress().getCountry().getAcronym());
            }

            if (this.representative.getPartner().getAddress().getProvince() != null) {
                locationMBean.setSelectedProvince(this.representative.getPartner().getAddress().getProvince().getId());
            }

            if (this.representative.getPartner().getAddress().getCity() != null) {
                locationMBean.setSelectedCity(this.representative.getPartner().getAddress().getCity().getId());
            }
        }

        loadLogoImage();
    }

    public String save() {
        Country country = this.locationMBean.getCountry();
        if (country != null) {
            this.representative.getPartner().getAddress().setCountry(country);
        }

        Province province = this.locationMBean.getProvince();
        if (province != null) {
            this.representative.getPartner().getAddress().setProvince(province);
        }

        City city = this.locationMBean.getCity();
        if (city != null) {
            this.representative.getPartner().getAddress().setCity(city);
        }

        partnerBean.save(this.representative.getPartner());
        representativeBean.save(this.representative);

        context.getExternalContext().getSessionMap().remove('locationBean');

        return 'profile?faces-redirect=true&tab=2';
    }

    public String remove() {
        representativeBean.remove(representative.getId());
        return 'profile?faces-redirect=true';
    }

    private void loadLogoImage() {
        try {
            String logoPath = this.representative.getPartner().getLogo();

            if (logoPath != null) {
                InputStream is = new FileInputStream(new File(logoPath));
                String[] logoPathArray = [logoPath] as String[]
                LOGGER.log(Level.INFO, 'JUG-0002: Loading logo file {0}', logoPathArray);
                logoImage = new DefaultStreamedContent(is, 'image/jpeg');
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void handleLogoFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        String[] logParams = [uploadedFile.getFileName(), uploadedFile.getContentType(), System.getProperty('java.io.tmpdir')] as String[]
        LOGGER.log(Level.INFO, 'JUG-0001: File {0} of type {1} temporarely uploaded to {2}', logParams);
        try {
            /* Loads the representative related to the logged user. */
            UserAccount person = userAccountBean.findByUsername(username);
            this.representative = representativeBean.findRepresentative(person);

            /* Write the inputStream to a FileOutputStream */
            InputStream is = uploadedFile.getInputstream();
            ApplicationProperty applicationProperty = applicationPropertyBean.findApplicationProperty(Properties.FILE_REPOSITORY_PATH);
            String fileExtension = uploadedFile.getFileName();
            fileExtension = fileExtension.substring(fileExtension.indexOf('.'));
            StringBuilder filePath = new StringBuilder();
            filePath.append(applicationProperty.getPropertyValue());
            filePath.append('/');
            filePath.append(this.representative.getPartner().getId());
            filePath.append(fileExtension);
            OutputStream out = new FileOutputStream(new File(filePath.toString()));
            int read;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            is.close();
            out.flush();
            out.close();

            /* If nothing goes wrong while saving the file,
             * then updates the database with the file location. */
            this.representative.getPartner().setLogo(filePath.toString());
            partnerBean.save(this.representative.getPartner());

            loadLogoImage();
        } catch (IOException ioe) {
            LOGGER.log(Level.INFO, ioe.getMessage(), ioe);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
        FacesMessage msg = new FacesMessage('Succesful', uploadedFile.getSize() + ' bytes of the file ' + uploadedFile.getFileName() + ' are uploaded.');
        context.addMessage(null, msg);
    }

    public String removeLogoImage() {
        try {
            String logoPath = this.representative.getPartner().getLogo();

            if (logoPath != null) {
                File logo = new File(logoPath);
                logo.delete();
                InputStream is = new FileInputStream(new File(logoPath));
                String[] logoPathArray = [logoPath] as String[]
                LOGGER.log(Level.INFO, 'JUG-0002: Loading logo file {0}', logoPathArray);
                logoImage = new DefaultStreamedContent(is, 'image/jpeg');
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return 'profile?faces-redirect=true&tab=2';
    }
}
