package com.insider.kontrollactiveReports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class NaeringsbyggQualityReport extends ActionBarActivity implements ReportInterface {

	SignatureView drawView;
	static final int REQUEST_TAKE_PHOTO = 1;
	ArrayList<File> pictureList;
	ArrayList<Email> emailList;
	ArrayList<String> stringList;
	ArrayList<String> pictureDokumentationList;
	String[] checkBoxChoices;
	String picturePath, attachementPath, pictureDokuString ="";
	String signFilePath;
	String date;
	Customer cust;
	User user;
	String msg;
	Context context = this;
	
	TextView kontaktperson, arbeidsplassmappe, kundemappe, gulvrenhold_field, tepper_field, harde_field, dorer_handtak_field, gulvlister_field, vegger_field,
			lysbrytere_soyler_field, sekundare_flater_field, andre_stovflater_field, rulletrapp_field, bjerlker_atrium_field, hygiene_sanitar_field, handvask_field, wc_field,  
			dorflater_field, vegg_field, annet_field, under_faste_matter_field, glass_inngangsparti_field, gelender_trapper_field, temp_field, bemerkninger_field;
	
	Spinner tepper_spinner, harde_spinner, gulvlister_spinner, vegger_spinner, lysbrytere_soyler_spinner, andre_stovflater_spinner, rulletrapp_spinner, bjerlker_spinner,
			handvask_spinner, wc_spinner, vegg_spinner, dorflater_spinner, under_faste_matter_spinner, glass_inngangsparti_spinner, gelender_trapper_spinner;
	
	EditText kontaktperson_text, 	tepper_text,harde_text,gulvlister_text,vegger_text, lysbrytere_soyler_text,andre_stovflater_text,rulletrapp_text,bjerlker_text,
			handvask_text,wc_text,dorflater_text, vegg_text, under_faste_matter_text, glass_inngangsparti_text, gelender_trapper_text, temp_text, bemerkninger_text;
	
	RadioGroup radio_arbeidsplassmappe, 	radio_kundemappe, radio_temp;
	
	RadioButton radio_arbeidsplassmappe_yes, radio_arbeidsplassmappe_no, radio_kundemappe_yes, radio_kundemappe_no, radio_temp_yes, radio_temp_no;
	
	Button signature_button, generatePDF_button;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.naeringsbygg_quality_report);
		this.setTitle("Kvalitetsrapport NÊringsbygg");
		
		msg = "";
		date = new Date().getDate();
		pictureList = new ArrayList<File>();
		emailList = Globals.emaiList;
		stringList = new ArrayList<String>();
		signFilePath ="";
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		cust = b.getParcelable("customerObject");
		user = b.getParcelable("userObject");
		identifyTextView();
		identifyEditText();
		identifySpinners();
		identifyRadioGroup();
		identifyRadioButtons();
		identifyButtons();
		
		
		generatePDF_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					createPDF();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(v.getContext(), "Rapport laget!", Toast.LENGTH_LONG)
				.show();
				
				
				try {
					sendPDF();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		});
		
		signature_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signatureDialog();
				
			}
		});
		
	}

	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quality_report, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_camera:
	        	dispatchTakePictureIntent();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void signatureDialog(){
		
		final Dialog signDialog = new Dialog(context);
		signDialog.setTitle("Sign√©r i det hvite feltet");
		signDialog.setContentView(R.layout.signature_dialog_view);
		drawView = (SignatureView)signDialog.findViewById(R.id.drawing);
		Button okButton = (Button) signDialog.findViewById(R.id.signature_dialog_okButton);
		Button cancelButton = (Button) signDialog.findViewById(R.id.signature_dialog_cancelButton);
		
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				drawView.setDrawingCacheEnabled(true);
				String signFileName = Environment.getExternalStorageDirectory()+"/insider_data/sign.png";
				OutputStream stream;
				try {
					stream = new FileOutputStream(signFileName);
					drawView.getDrawingCache().compress(CompressFormat.PNG, 80, stream);
					Toast.makeText(context, "Signatur lagret i pdf!", Toast.LENGTH_SHORT).show();
					signFilePath = signFileName;
					Log.d("!!Sign",signFilePath);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		    	drawView.destroyDrawingCache();
		    	signDialog.dismiss();
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				signDialog.dismiss();
				
			}
		});
	
		signDialog.show();
	}
	@Override
	public void sendPDF() throws Exception{
		int type = 3;
		 
		EmailGenerator gen = new EmailGenerator(this, cust, date, msg, emailList,attachementPath, type, Globals.user.getId());
		gen.sendEmail();
		finish();
	}

	@Override
	public void createPDF() throws IOException, DocumentException {
		
		int  arbeidsplassMappeSelected = radio_arbeidsplassmappe.getCheckedRadioButtonId();
		String text = "";
		String src = Environment.getExternalStorageDirectory()
                 + "/insider_data/templates/rapport_naeringsbygg.pdf";
         String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/naeringsbygg_kvalitetsrapport_"+date+".pdf"; 
        
         PdfReader reader = new PdfReader(src);
         PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dst));
         
         AcroFields form = stamper.getAcroFields();

         
         form.setField("date_field", date);
         form.setField("executor_field", "Thomas Franang");
         form.setField("customer_field", cust.getName());
         form.setField("department_field", cust.getDepartment());
         form.setField("type_of_report_field", "Kvalitetsrapport Helsebygg");
         
         form.setField("participant_field", kontaktperson_text.getText().toString());
         if(radio_arbeidsplassmappe_yes.isChecked()){
         	form.setField("arbeidsplassmappe_box_yes","Yes");
         }
         else{
         	 form.setField("arbeidsplassmappe_box_no", "Yes");
         }
         if(radio_kundemappe_yes.isChecked()){
         	form.setField("kundemappe_box_yes","Yes");
          }
          else{
         	 form.setField("kundemappe_box_no", "Yes");
          }
         
         if(radio_temp_yes.isChecked()){
         	form.setField("temp_box_yes","Yes");
         }
         
         else{
         	 form.setField("temp_box_no", "Yes");
         }
         
         
         form.setField("tepper_field", tepper_spinner.getSelectedItem().toString());
         form.setField("tepper_beskrivelse_field", tepper_text.getText().toString());
         
         form.setField("harde_field", harde_spinner.getSelectedItem().toString());
         form.setField("harde_beskrivelse_field", harde_text.getText().toString());
         
         form.setField("gulvlister_field", gulvlister_spinner.getSelectedItem().toString());
         form.setField("gulvlister_beskrivelse_field", gulvlister_text.getText().toString());
         
         form.setField("vegger_field", vegger_spinner.getSelectedItem().toString());
         form.setField("vegger_beskrivelse_field", vegger_text.getText().toString());
         
         form.setField("lysbrytere_field", lysbrytere_soyler_spinner.getSelectedItem().toString());
         form.setField("lysbrytere_beskrivelse_field", lysbrytere_soyler_text.getText().toString());
         
         form.setField("andre_flater_field", andre_stovflater_spinner.getSelectedItem().toString());
         form.setField("andre_flater_beskrivelse_field", andre_stovflater_text.getText().toString());
         
         form.setField("rulletrapp_field", rulletrapp_spinner.getSelectedItem().toString());
         form.setField("rulletrapp_beskrivelse_field", rulletrapp_text.getText().toString());
         
         form.setField("bjerlker_field", bjerlker_spinner.getSelectedItem().toString());
         form.setField("bjerlker_beskrivelse_field", bjerlker_text.getText().toString());
         
         form.setField("handvask_field", handvask_spinner.getSelectedItem().toString());
         form.setField("handvask_beskrivelse_field", handvask_text.getText().toString());        
         form.setField("wc_field", wc_spinner.getSelectedItem().toString());
         form.setField("wc_beskrivelse_field", wc_text.getText().toString());
         
         form.setField("sapedispenser_field",vegg_spinner.getSelectedItem().toString());
         form.setField("sapedispenser_beskrivelse_field", dorflater_text.getText().toString());
         
         form.setField("speil_field", vegg_spinner.getSelectedItem().toString());
         form.setField("speil_beskrivelse_field", vegg_text.getText().toString());
         
         form.setField("faste_matter_field", under_faste_matter_spinner.getSelectedItem().toString());
         form.setField("faste_matter_beskrivelse_field", under_faste_matter_text.getText().toString());
         
         form.setField("glass_field", glass_inngangsparti_spinner.getSelectedItem().toString());
         form.setField("glass_beskrivelse_field", glass_inngangsparti_text.getText().toString());
         
         form.setField("gelender_trapper_field", gelender_trapper_spinner.getSelectedItem().toString());
         form.setField("gelender_trapper_beskrivelse_field", gelender_trapper_text.getText().toString());
         
         form.setField("temp_beskrivelse_field", temp_text.getText().toString());
         
         form.setField("bemerkninger_field", bemerkninger_text.getText().toString());
         
         
         if(pictureList.size() != 0){
        	 addPicturesToPDF(reader, stamper);
         }
         
         for (int i = 0; i < stringList.size(); i++) {
 			pictureDokuString += stringList.get(i)+" ";
 		}
         
         if(signFilePath != ""){
        	 addSignatureToPDF(reader, stamper);
         }
         
         form.setField("picures_field", pictureDokuString);
        
         form.setField("sign_field", cust.getDepartment()+", "+kontaktperson_text.getText().toString());
         
         stamper.setFormFlattening(true);
         stamper.close();
         reader.close();
         attachementPath = dst;
         
	}

	@Override
	public void addSignatureToPDF(PdfReader reader, PdfStamper stamper)
			throws MalformedURLException, IOException, DocumentException {
		
		PdfContentByte overContent = stamper.getOverContent(1);
		
		Image image = Image.getInstance(signFilePath);
		image.setAbsolutePosition(292,60);
		image.scaleAbsolute(190,20);
		overContent.addImage(image);
		
		File sign = new File(signFilePath);
		sign.delete();
		
	}

	@Override
	public void addPicturesToPDF(PdfReader reader, PdfStamper stamper) throws MalformedURLException, IOException, DocumentException{
		stamper.insertPage(reader.getNumberOfPages() + 1, reader.getPageSizeWithRotation(1));
		PdfContentByte overContent = stamper.getOverContent(2);    
		
		int xPos = 50;
		int yPos = 610;
		int count = 0;
		
		for (int i = 0; i < pictureList.size(); i++) {
			 Image image1 = Image.getInstance(pictureList.get(i).getAbsolutePath());          
		     
			 if(count < 2){
				 if(count == 1){
					 xPos += 250;
					 image1.setAbsolutePosition(xPos,yPos);    
					 image1.scaleAbsolute(280,200);
					 ColumnText.showTextAligned(overContent, Element.ALIGN_LEFT, new Phrase( stringList.get(i).toString()), xPos, yPos-10, 0);
					 count ++;
				 }
				 image1.setAbsolutePosition(xPos,yPos);    
				 image1.scaleAbsolute(240,200);
				 ColumnText.showTextAligned(overContent, Element.ALIGN_LEFT, new Phrase(stringList.get(i).toString()), xPos, yPos-10, 0);
				 count++;
			 }
			 else{
				 yPos -= 220;
				 xPos = 50;
				 image1.setAbsolutePosition(xPos,yPos);    
			     image1.scaleAbsolute(240,200);
			     ColumnText.showTextAligned(overContent, Element.ALIGN_LEFT, new Phrase(stringList.get(i).toString()), xPos, yPos-10, 0);
			     count = 0;
			 }
		        // change the content on top of page 1  
		        overContent.addImage(image1);

		}
		
		for (int i = 0; i < pictureList.size(); i++) {
			pictureList.get(i).delete();
		}
	}

	@Override
	public void onCheckboxClicked(View view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispatchTakePictureIntent() {

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
		          Log.d("!!Pic1", photoFile.getAbsolutePath());
		        
		          
		          
		          pictureList.add(photoFile);
		          stringList.add("bilde."+pictureList.size());
		        }
		    	}
		
	}

	@Override
	public File createImageFile() throws IOException {
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

	@Override
	public void identifyTextView() {
		kontaktperson = (TextView) findViewById(R.id.kontaktperson);
		arbeidsplassmappe = (TextView) findViewById(R.id.arbeidsplassmappe);
		kundemappe = (TextView) findViewById(R.id.kundemappe);
		gulvrenhold_field = (TextView) findViewById(R.id.gulvrenhold_field);
		tepper_field = (TextView) findViewById(R.id.tepper_field);
		harde_field = (TextView) findViewById(R.id.harde_field);
		dorer_handtak_field = (TextView) findViewById(R.id.dorer_handtak_field);
		gulvlister_field = (TextView) findViewById(R.id.gulvlister_field);
		vegger_field = (TextView) findViewById(R.id.vegger_field);
		lysbrytere_soyler_field = (TextView) findViewById(R.id.lysbrytere_soyler_field);
		sekundare_flater_field = (TextView) findViewById(R.id.sekundare_flater_field);
		andre_stovflater_field = (TextView) findViewById(R.id.andre_stovflater_field);
		rulletrapp_field = (TextView) findViewById(R.id.rulletrapp_field);
		bjerlker_atrium_field = (TextView) findViewById(R.id.bjerlker_atrium_field);
		hygiene_sanitar_field = (TextView) findViewById(R.id.hygiene_sanitar_field);
		handvask_field = (TextView) findViewById(R.id.handvask_vegg_field);
		wc_field = (TextView) findViewById(R.id.wc_field);
		dorflater_field = (TextView) findViewById(R.id.dorflater_field);
		vegg_field = (TextView) findViewById(R.id.vegg_field);
		annet_field = (TextView) findViewById(R.id.annet_field);
		under_faste_matter_field = (TextView) findViewById(R.id.under_faste_matter_field);
		glass_inngangsparti_field = (TextView) findViewById(R.id.glass_inngangsparti_field);
		gelender_trapper_field = (TextView) findViewById(R.id.gelender_trapper_field);
		temp_field = (TextView) findViewById(R.id.temp_field);
		bemerkninger_field = (TextView) findViewById(R.id.bemerkninger_field);

		
	}

	@Override
	public void identifyCheckBox() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identifyEditText() {
		kontaktperson_text = (EditText) findViewById(R.id.kontaktperson_text);
		tepper_text = (EditText) findViewById(R.id.tepper_text);
		harde_text = (EditText) findViewById(R.id.harde_text);
		gulvlister_text = (EditText) findViewById(R.id.gulvlister_text);
		vegger_text = (EditText) findViewById(R.id.vegger_text);
		lysbrytere_soyler_text = (EditText) findViewById(R.id.lysbrytere_soyler_text);
		andre_stovflater_text = (EditText) findViewById(R.id.andre_stovflater_text);
		rulletrapp_text = (EditText) findViewById(R.id.rulletrapp_text);
		bjerlker_text = (EditText) findViewById(R.id.bjerlker_text);
		handvask_text = (EditText) findViewById(R.id.handvask_text);
		wc_text = (EditText) findViewById(R.id.wc_text);
		dorflater_text = (EditText) findViewById(R.id.dorflater_text);
		vegg_text = (EditText) findViewById(R.id.vegg_text);
		under_faste_matter_text = (EditText) findViewById(R.id.under_faste_matter_text);
		glass_inngangsparti_text = (EditText) findViewById(R.id.glass_inngangsparti_text);
		gelender_trapper_text = (EditText) findViewById(R.id.gelender_trapper_text);
		bemerkninger_text = (EditText) findViewById(R.id.bemerkninger_text);
		temp_text = (EditText) findViewById(R.id.temp_text);
		
	}

	@Override
	public void identifySpinners() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.butikk_barnehage_naringsbygg_spinner_choices, android.R.layout.simple_spinner_dropdown_item);
		
		tepper_spinner = (Spinner) findViewById(R.id.tepper_spinner);
		tepper_spinner.setAdapter(adapter);
		harde_spinner = (Spinner) findViewById(R.id.harde_spinner);
		harde_spinner.setAdapter(adapter);
		gulvlister_spinner = (Spinner) findViewById(R.id.gulvlister_spinner);
		gulvlister_spinner.setAdapter(adapter);
		vegger_spinner = (Spinner) findViewById(R.id.vegger_spinner);
		vegger_spinner.setAdapter(adapter);
		lysbrytere_soyler_spinner = (Spinner) findViewById(R.id.lysbrytere_soyler_spinner);
		lysbrytere_soyler_spinner.setAdapter(adapter);
		andre_stovflater_spinner = (Spinner) findViewById(R.id.andre_stovflater_spinner);
		andre_stovflater_spinner.setAdapter(adapter);
		rulletrapp_spinner = (Spinner) findViewById(R.id.rulletrapp_spinner);
		rulletrapp_spinner.setAdapter(adapter);
		bjerlker_spinner = (Spinner) findViewById(R.id.bjerlker_spinner);
		bjerlker_spinner.setAdapter(adapter);
		handvask_spinner = (Spinner) findViewById(R.id.handvask_spinner);
		handvask_spinner.setAdapter(adapter);
		wc_spinner = (Spinner) findViewById(R.id.wc_spinner);
		wc_spinner.setAdapter(adapter);
		vegg_spinner = (Spinner) findViewById(R.id.vegg_spinner);
		vegg_spinner.setAdapter(adapter);
		dorflater_spinner = (Spinner) findViewById(R.id.dorflater_spinner);
		dorflater_spinner.setAdapter(adapter);
		under_faste_matter_spinner = (Spinner) findViewById(R.id.under_faste_matter_spinner);
		under_faste_matter_spinner.setAdapter(adapter);
		glass_inngangsparti_spinner = (Spinner) findViewById(R.id.glass_inngangsparti_spinner);
		glass_inngangsparti_spinner.setAdapter(adapter);
		gelender_trapper_spinner = (Spinner) findViewById(R.id.gelender_trapper_spinner);
		gelender_trapper_spinner.setAdapter(adapter);
		
	}

	@Override
	public void identifyRadioButtons() {
		radio_arbeidsplassmappe_yes = (RadioButton) findViewById(R.id.arbeidsplassmappe_yes);
		radio_arbeidsplassmappe_no = (RadioButton) findViewById(R.id.arbeidsplassmappe_no);
		radio_kundemappe_yes = (RadioButton) findViewById(R.id.kundemappe_yes);
		radio_kundemappe_no = (RadioButton) findViewById(R.id.kundemappe_no);
		radio_temp_yes = (RadioButton) findViewById(R.id.temp_yes);
		radio_temp_no = (RadioButton) findViewById(R.id.temp_no);
		
	}

	@Override
	public void identifyRadioGroup() {
		 radio_arbeidsplassmappe = (RadioGroup) findViewById(R.id.radio_arbeidsplassmappe);
		 radio_kundemappe = (RadioGroup) findViewById(R.id.radio_kundemappe);
		 radio_temp = (RadioGroup) findViewById(R.id.radio_temp);
		
	}

	@Override
	public void identifyButtons() {
		signature_button = (Button) findViewById(R.id.sign_button);
		generatePDF_button = (Button) findViewById(R.id.generatePDF_button);
		
	}
}
