package com.mdc.elementary.alsagps;

/*
   AlsaGPS is a panic button app for Android
   Copyright (C) 2016 Moisés Díaz

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA

*/

public class Contact{
    private String name;
    private String number;

    @Override
    public String toString(){
        return name;
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public void setName(String new_name){
        if(!new_name.isEmpty())
            this.name=new_name;
    }

    public void setNumber(String new_number){
        Double val = null;
        String new_number_2 = new_number.replaceAll("[^\\d]", "");
        try {
            val = Double.valueOf(new_number_2);
        }catch (NumberFormatException e){
            if(new_number_2 != null) {
                this.number=new_number;
            }
        }
        if(val != null)
            this.number=new_number;
    }
}