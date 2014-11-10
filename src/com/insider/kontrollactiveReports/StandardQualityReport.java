package com.insider.kontrollactiveReports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.insider.kontrollactive.Email;
import com.insider.kontrollactive.EmailGenerator;
import com.insider.kontrollactive.R;
import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Date;
import com.insider.kontrollactiveModel.Globals;
import com.insider.kontrollactiveModel.User;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class StandardQualityReport extends ActionBarActivity {

	static final int REQUEST_TAKE_PHOTO = 1;
	ArrayList<File> pictureList;
	ArrayList<Email> emailList;
	String[] checkBoxChoices;
	String picturePath, attachementPath;
	String date;
	Customer cust;
	User user;
	String msg;
	
	RadioGroup radio_arbeidsplassmappe;
	TextView arbeidsplassmappe, kontaktperson,gulv_tepper,gulv_harde,kommentar_gulv, sekundare_flater,kommentar_sekundare_flater,hygiene_sanitar, 
	kommentar_hygiene_sanitar, miljo_insider, kommentar_miljo_insider, lunsj_inside, kommentar_lunsj_inside, forskjell_etter_insider, fornoyd_med_insider,
	medarbeidere_question, generell_kommentar;
	EditText kontaktperson_text, kommentar_gulv_text, kommentar_sekundare_flater_text, hygiene_sanitar_text, lunsj_inside_text, generell_kommentar_text;
	Spinner gulv_tepper_spinner, gulv_harde_spinner, sekundare_flater_spinner, hygiene_sanitar_spinner, miljo_inside_spinner,lunsj_inside_spinner, forskjell_etter_inside_spinner,
	fornoyd_med_inside_spinner, medarbeidere_question_spinner;
	Button camera_button, pdf_button;
	RadioButton arbeidsplassmappe_ja, arbeidsplassmappe_nei;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standard_quality_report);
		this.setTitle("Standard Kvalitetsrapport");
		
		msg = "";
		date = new Date().getDate();
		pictureList = new ArrayList<File>();
		emailList = Globals.emaiList;
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		cust = b.getParcelable("customerObject");
		user = b.getParcelable("userObject");
		identifyTextView();
		identifyEditText();
		identifySpinners();
		identifyRadioGroup();
		identifyButtons();
		
		camera_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});
		
		pdf_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					createPDF();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(v.getContext(), "Done!", Toast.LENGTH_LONG)
				.show();
				
				
				try {
					sendPDF();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		});
	}
	
	public void sendPDF() throws Exception{
		int type = 3;
		 
		EmailGenerator gen = new EmailGenerator(this, cust, date, msg, emailList,attachementPath, type);
		gen.sendEmail();
		
	}
	
	public void createPDF() throws IOException, DocumentException{
		int  arbeidsplassMappeSelected = radio_arbeidsplassmappe.getCheckedRadioButtonId();
		String text = "";
		Log.d("!!", cust.getName());
		
		
		String src = Environment.getExternalStorageDirectory()
                 + "/insider_data/rapport_standard.pdf";
         String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/standard_kvalitetsrapport_"+date+".pdf"; 
        
         PdfReader reader = new PdfReader(src);
         PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dst));
         
         AcroFields form = stamper.getAcroFields();

         form.setField("date_in_header_field", date);
         form.setField("executor_field", "Thomas");
         form.setField("customer_field", cust.getName());
         form.setField("department_field", cust.getDepartment());
         form.setField("type_of_report_field", "Standard kvalitetsrapport");
         form.setField("date_in_body_field", date);
        
         Log.d("!!text", ""+arbeidsplassmappe_ja.isChecked());
         if(arbeidsplassMappeSelected == 0){
        	String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
        	form.setField("arbeidsplassmappe_box_yes","Yes");
         }
         else{
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_no");
        	 form.setField("arbeidsplassmappe_box_no", "Yes");
         }
         form.setField("contact_field", kontaktperson_text.getText().toString());
         
         text = gulv_tepper_spinner.getSelectedItem().toString();
         Log.d("!!text",text);
         if(text != "Ingen valg" || text != ""){
    		 form.setField("floor_carpets_field", text);
        	 String[] values = form.getAppearanceStates("floor_carpet_box");
        	 form.setField("floor_carpet_box", "Yes");
         }
         
//         text = gulv_harde_spinner.getSelectedItem().toString();
//         if(text != "Ingen valg"){
//    		 form.setField("floor_hard_field", text);
//        	 String[] values = form.getAppearanceStates("floor_hard_box");
//        	 form.setField("floor_hard_box", "Yes");
//         }
         
         stamper.setFormFlattening(true);
         stamper.close();
         reader.close();
         attachementPath = dst;
        
	}
	
	public void getCheckBoxValue(String src, String[] values) throws IOException{
		PdfReader reader = new PdfReader(src);
        AcroFields fields = reader.getAcroFields();
        StringBuffer sb = new StringBuffer();
        for (String value : values) {
            sb.append(value);
            sb.append('\n');
        }
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	            
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	          startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	          pictureList.add(photoFile);
	          Log.d("!!",""+pictureList.size());
	        }
	    	}
	    }
	
	public File createImageFile() throws IOException {
	    // Create an image file name
	       
	    
		String imageFileName = cust.getEmail()+"photo"+pictureList.size();
	    picturePath = Environment.getExternalStorageDirectory()+"/insider_data/";
	    
	    File storageDir = new File(picturePath);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    picturePath = "file:" + image.getAbsolutePath();
	    return image;
	}
	
	public void identifyTextView(){
	
		arbeidsplassmappe = (TextView)findViewById(R.id.arbeidsplassmappe);
		kontaktperson = (TextView)findViewById(R.id.kontaktperson);
		gulv_tepper = (TextView)findViewById(R.id.gulv_tepper);
		gulv_harde = (TextView)findViewById(R.id.gulv_harde);
		kommentar_gulv = (TextView)findViewById(R.id.kommentar_gulv);
		sekundare_flater = (TextView)findViewById(R.id.sekundare_flater);
		kommentar_sekundare_flater = (TextView)findViewById(R.id.kommentar_sekundare_flater);
		hygiene_sanitar = (TextView)findViewById(R.id.hygiene_sanitar); 
		kommentar_hygiene_sanitar = (TextView)findViewById(R.id.kommentar_hygiene_sanitar); 
		miljo_insider = (TextView)findViewById(R.id.miljo_insider); 
		kommentar_miljo_insider = (TextView)findViewById(R.id.kommentar_miljo_insider); 
		lunsj_inside = (TextView)findViewById(R.id.lunsj_inside); 
		kommentar_lunsj_inside = (TextView)findViewById(R.id.kommentar_lunsj_inside);
		forskjell_etter_insider = (TextView)findViewById(R.id.forskjell_etter_insider); 
		fornoyd_med_insider =  (TextView)findViewById(R.id.fornoyd_med_insider);
		medarbeidere_question  = (TextView)findViewById(R.id.medarbeidere_question);
		generell_kommentar = (TextView)findViewById(R.id.generell_kommentar);
		
		
	}
	
	
	public void identifyEditText(){
		
		kontaktperson_text = (EditText) findViewById(R.id.kontaktperson_text);
		kommentar_gulv_text= (EditText) findViewById(R.id.kommentar_gulv_text);
		kommentar_sekundare_flater_text = (EditText) findViewById(R.id.kommentar_sekundare_flater_text);
		hygiene_sanitar_text = (EditText) findViewById(R.id.hygiene_sanitar_text);
		lunsj_inside_text = (EditText) findViewById(R.id.lunsj_inside_text);
		generell_kommentar_text = (EditText) findViewById(R.id.generell_kommentar_text);
		
	}
	
	public void identifySpinners(){
		
		gulv_tepper_spinner = (Spinner) findViewById(R.id.gulv_tepper_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.normal_spinner_choices, android.R.layout.simple_spinner_dropdown_item);
		gulv_tepper_spinner.setAdapter(adapter);
		
		gulv_harde_spinner = (Spinner) findViewById(R.id.gulv_hard_spinner);
		gulv_harde_spinner.setAdapter(adapter);
		
		sekundare_flater_spinner  = (Spinner) findViewById(R.id.sekundare_flater_spinner);
		sekundare_flater_spinner.setAdapter(adapter);
		
		hygiene_sanitar_spinner  = (Spinner) findViewById(R.id.hygiene_sanitar_spinner);
		hygiene_sanitar_spinner.setAdapter(adapter);
		
		miljo_inside_spinner = (Spinner) findViewById(R.id.miljo_insider_spinner);
		miljo_inside_spinner.setAdapter(adapter);
		
		lunsj_inside_spinner = (Spinner) findViewById(R.id.lunsj_inside_spinner);
		lunsj_inside_spinner.setAdapter(adapter);
		
		forskjell_etter_inside_spinner  = (Spinner) findViewById(R.id.forskjell_etter_insider_spinner); 
		ArrayAdapter<CharSequence> extensiveAdapter = ArrayAdapter.createFromResource(this, R.array.extended_spinner_choices, android.R.layout.simple_spinner_dropdown_item);
		forskjell_etter_inside_spinner.setAdapter(extensiveAdapter);
		
		fornoyd_med_inside_spinner  = (Spinner) findViewById(R.id.fornoyd_med_insider_spinner);
		fornoyd_med_inside_spinner.setAdapter(extensiveAdapter);
		
		medarbeidere_question_spinner  = (Spinner) findViewById(R.id.medarbeidere_question_spinner);
		medarbeidere_question_spinner.setAdapter(extensiveAdapter);
		
		
	}
	
	public void identifyRadioButtons(){
		
		arbeidsplassmappe_ja = (RadioButton) findViewById(R.id.arbeidsplassmappe_yes);
		arbeidsplassmappe_nei = (RadioButton) findViewById(R.id.arbeidsplassmappe_no);
		
	}
	
	
	public void identifyRadioGroup(){
		radio_arbeidsplassmappe = (RadioGroup) findViewById(R.id.radio_arbeidsplassmappe);
	}
	
	public void identifyButtons(){
		
		camera_button = (Button) findViewById(R.id.take_picture_button);
		pdf_button = (Button) findViewById(R.id.generatePDF_button);
		
	}
}
