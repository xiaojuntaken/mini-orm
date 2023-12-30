package org.hxj.utils;

public class StringUtils {

    /**
     *
     * @param camelCaseString
     * @return
     */
    public static String toUnderScoreCase(String camelCaseString){
        StringBuilder underScoreString = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            if(Character.isUpperCase(camelCaseString.charAt(i))){
                if(i==0){
                    underScoreString.append(Character.toLowerCase(camelCaseString.charAt(i)));
                }else{
                    underScoreString.append("_").append(Character.toLowerCase(camelCaseString.charAt(i)));
                }
            }else{
                underScoreString.append(Character.toLowerCase(camelCaseString.charAt(i)));
            }
        }
        return underScoreString.toString();
    }
}
