package com.namocode.restorecontacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import android.os.Environment;


public class IO 
{
	public static Boolean Write(List<String> texts , File file) throws IOException
	{		 
		FileOutputStream fileout = null;
		
		try
		{
			fileout = new FileOutputStream(file);
		}
		catch(Exception Ex)
		{
			return false;		
		}
		
		OutputStreamWriter writer = new OutputStreamWriter(fileout);
		
		try
		{				
			for(String text : texts)
			{
				writer.write(text);
				writer.write(new char[] { '\r' , '\n'});
				writer.flush();
			}
			
		}
		catch(Exception ex)
		{
			return false;
		}
		finally
		{
			writer.close();	
		}
	
		return true;
	}
		
}
