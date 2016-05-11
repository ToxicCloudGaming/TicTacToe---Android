package org.toxiccloudgaming.client.http.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.toxiccloudgaming.tictactoe.client.ActionsList;

import java.util.HashMap;

public abstract class JSONAction {

    public static String createJSONString(String action, HashMap<String, Object> params) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(ActionsList.PARAMS_ACTION, action);

        for(HashMap.Entry<String, Object> param : params.entrySet()) {
            if(param.getClass().isArray()) {
                JSONArray array = new JSONArray();
                for(Object object : (Object[])param.getValue()) {
                    array.put(object);
                }

                json.put(param.getKey(), array);
            } else {
                json.put(param.getKey(), param.getValue());
            }
        }

        return json.toString();
    }

    public static JSONObject createJSONObj(String jsonString) {
        try {
            JSONTokener tokener = new JSONTokener(jsonString);
            return (JSONObject)tokener.nextValue();
        } catch(Exception e) {
            return null;
        }
    }

    public static String formatJSON(String jsonString, int spaces) {
        String newJsonString = null;
        StringBuilder sb = new StringBuilder();
        char[] jsonChars = jsonString.toCharArray();

        int tabs = 0;
        int index = 0;
        boolean inQuotes = false;
        boolean backSlash = false;
        boolean newLine = false;
        boolean isNumber = false;
        boolean isBoolean = false;

        for(char c : jsonChars) {
            index++;

            if(newLine) {
                sb.append("\n");
                for (int i = 0; i < (tabs * spaces); i++) {
                    sb.append(" ");
                }

                newLine = false;
            }

            if(inQuotes) {
                if(backSlash) {
                    backSlash = false;
                } else if(!backSlash && c =='\\'){
                    backSlash = true;
                } else {
                    sb.append(c);
                    if(c == '"') {
                        inQuotes = false;
                    }
                }
            } else {
                if(c != ' ') {
                    if(c == '"') {
                        sb.append(c);
                        inQuotes = true;
                    } else if(c == ':') {
                        sb.append(" " + c + " ");
                    } else if(c == '{' || c == '[') {
                        sb.append(c);
                        tabs++;
                        newLine = true;
                    } else if(c == '}' || c == ']') {
                        sb.append("\n");
                        tabs--;
                        for(int i = 0; i < (tabs * spaces); i++) {
                            sb.append(" ");
                        }
                        sb.append(c);
                        newLine = true;
                    } else if(c == ',') {
                        sb.append(c);
                        newLine = true;
                    } else {
                        sb.append(c);
                    }
                }
            }
        }

        newJsonString = sb.toString();
        return newJsonString;
    }
}
