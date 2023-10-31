/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.etradis.core.dppedia.helper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
public class Filter {

    public static List<String> getFilterResults(List<String> predicate_subects) {
        List<String> result=new ArrayList<String>();
        for(String subj:predicate_subects){
            String []info=subj.split(" ");
            String property=info[1];
            String subOrObjUri=info[0];
            if(predicate_subects.contains(property))
                result.add(subOrObjUri);
        }
        return result;
    }
    
}
