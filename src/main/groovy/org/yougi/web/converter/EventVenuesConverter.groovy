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
package org.yougi.web.converter;

import org.yougi.event.entity.Event;
import org.yougi.event.entity.Venue;
import org.yougi.util.ResourceBundleHelper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@FacesConverter(value="EventVenuesConverter")
public class EventVenuesConverter  implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null || !(value instanceof Event)) {
            return null;
        }

        Event event = (Event) value;

        if(event.getVenues() == null || event.getVenues().isEmpty()) {
            return null;
        }

        StringBuilder strVenues = new StringBuilder();
        strVenues.append(ResourceBundleHelper.getMessage("at"));
        strVenues.append(" ");
        String and = "";
        for(Venue venue: event.getVenues()) {
            strVenues.append(and);
            if("".equals(and)) {
                and = " " + ResourceBundleHelper.getMessage("and") + " ";
            }
            strVenues.append("<a href=\"venue.xhtml?id=");
            strVenues.append(venue.getId());
            strVenues.append("&eventId=");
            strVenues.append(event.getId());
            strVenues.append("\">");
            strVenues.append(venue.getName());
            strVenues.append("</a>");
        }
        return strVenues.toString();
    }
}
