/* Copyright (C) 2000-2009

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; version 2 of the License.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA */

package com.qbrowser.localstore.genericdest;


/**
 *
 * @author takemura
 */
public class LocalDestination extends com.sun.messaging.Destination implements javax.jms.Destination {
    private String product;
    private String originalDestinationWithSuffix;
    String nameofthis;

    @Override
    public boolean isQueue() {
        return true;
    }

    @Override
    public boolean isTemporary() {
        return false;
    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the originalDestinationWithSuffix
     */
    public String getOriginalDestinationWithSuffix() {
        return originalDestinationWithSuffix;
    }

    /**
     * @param originalDestinationWithSuffix the originalDestinationWithSuffix to set
     */
    public void setOriginalDestinationWithSuffix(String originalDestinationWithSuffix) {
        this.originalDestinationWithSuffix = originalDestinationWithSuffix;
    }


}
