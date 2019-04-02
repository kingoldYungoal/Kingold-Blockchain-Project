package com.kingold.educationblockchain.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class StringNullAdapter extends TypeAdapter<String> {
    @Override
    public String read(JsonReader reader) throws IOException {
        // TODO Auto-generated method stub
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return "";
        }
        if(reader.peek() == JsonToken.STRING){
        	String value = reader.nextString();
        	
        	if(value.equals("null")) {
        		return "未知";
        	}
        	
        	return value;
        }
        
        return reader.nextString();
    }
    @Override
    public void write(JsonWriter writer, String value) throws IOException {
        // TODO Auto-generated method stub
        if (value == null || value.equalsIgnoreCase("null")) {
            writer.value("未知");
            return;
        }
        writer.value(value);
    }
}