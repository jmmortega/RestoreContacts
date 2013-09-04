package com.namocode.restorecontacts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private List<GoogleCSV> contactsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		v.vibrate(500);
		
		contactsList = new ArrayList<GoogleCSV>();
		
		readContacts();
		
		Log.d("ReadComplete" , "Yeah");
		
		writingContacts();
		
		v.vibrate(500);
		v.vibrate(500);
		v.vibrate(500);
		v.vibrate(2000);
		
		Log.d("Finish" , "Yeah");		
	}
	
	
	//Thanks Emran Hamza
	//http://stackoverflow.com/questions/4941965/getting-android-contacts
	public void readContacts()
	{
		
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

        Integer numberContacts = cur.getCount();
        
        Log.d("NumberContacts", numberContacts.toString());
        
        if (cur.getCount() > 0) 
        {
           while (cur.moveToNext()) 
           {
        	   GoogleCSV myContact = new GoogleCSV();
        	   
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
               {
                   Log.d("Name:Id", "name : " + name + ", ID : " + id);
                   myContact.setName(name);

                   // get the phone number
                   Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                          ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                          new String[]{id}, null);
                   
                   int phoneNumberCount = 0;
                   while (pCur.moveToNext()) 
                   {
                         String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                         
                         String phoneType = pCur.getString(
                        		pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                         Log.d("phone" , phone);
                         Log.d("type" , phoneType);
                         
                         if(phoneNumberCount == 0)
                         {
                        	 myContact.setPhone1Value(phone);
                        	 myContact.setPhone1Type(phoneType);
                        	 phoneNumberCount++;                         
                         }
                         else
                         {
                        	myContact.setPhone2Value(phone);
                        	myContact.setPhone2Type(phoneType);
                         }                         
                   }
                   pCur.close();
                 
                  // get email and type

                  Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                           null,
                           ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                           new String[]{id}, null);
                  
                  int mailCountNumber = 0;
                  while (emailCur.moveToNext()) 
                   {
                       // This would allow you get several email addresses
                           // if the email addresses were stored in an array
                       String email = emailCur.getString(
                                     emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                       String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                       int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                       String customLabel = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
                       CharSequence CustomemailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(this.getResources(), type, customLabel);
                       Log.d("Email", email);
                       Log.d("EmailType", emailType);
                       
                       if(mailCountNumber == 0)
                       {
                    	   myContact.setEmail1Value(email);
                    	   myContact.setEmail1Type(emailType);
                    	   mailCountNumber++;                       
                       }
                       else
                       {
                    	   myContact.setEmail2Value(email);
                    	   myContact.setEmail2Type(emailType);
                       }
                                            
                   }
                   
                   emailCur.close();

                   // Get note.......
                   String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                           ContactsContract.Data.MIMETYPE + " = ?";
                   String[] noteWhereParams = new String[]{id,
                   ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, 
                            noteWhere, noteWhereParams, null);
                   if (noteCur.moveToFirst()) 
                   {
                       String note = noteCur.getString(
                       noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                       Log.d("Note " , note);
                   }
                   noteCur.close();

                   //Get Postal Address....

                   String addrWhere = ContactsContract.Data.CONTACT_ID 
                           + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] addrWhereParams = new String[]{id,
                       ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                   Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                               null, null, null, null);
                   while(addrCur.moveToNext()) 
                   {
                       String poBox = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                       String street = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                       String city = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                       String state = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                       String postalCode = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                       String country = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                       String type = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                                                                   
                   }
                   addrCur.close();

                   // Get Instant Messenger.........
                   String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " 
                   + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] imWhereParams = new String[]{id,
                       ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                   Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                           null, imWhere, imWhereParams, null);
                   if (imCur.moveToFirst()) 
                   {
                       String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                       String imType;
                       imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                   }
                   
                   imCur.close();

                   // Get Organizations.........

                   String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                   String[] orgWhereParams = new String[]{id,
                       ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                   Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                               null, orgWhere, orgWhereParams, null);
                   if (orgCur.moveToFirst()) 
                   {
                       String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                       String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                   }
                   
                   orgCur.close();
               }
               
               contactsList.add(myContact);
           }
      }
   }

	public void writingContacts()
	{
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "myContactsFile.csv"); 
		
		Log.d("Stored In:", file.getAbsolutePath());

		List<String> dataFile = new ArrayList<String>();
		
		dataFile.add(GoogleCSV.Header);
					
		for(GoogleCSV csv : contactsList)		
		{
			dataFile.add(csv.ToCSV());			
		}
		
		Integer contactsCountStored = dataFile.size();
		
		Log.d("Number Contacts Stored", contactsCountStored.toString());
		
		try 
		{
			IO.Write(dataFile, file);			
		} 
		catch (IOException e) 
		{
			Log.d("Error" , e.getMessage());
		}
	}
}
