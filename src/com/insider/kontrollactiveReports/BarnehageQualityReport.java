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

public class BarnehageQualityReport extends ActionBarActivity implements ReportInterface{
	
	static final int REQUEST_TAKE_PHOTO = 1;
	ArrayList<File> pictureList;
	ArrayList<Email> emailList;
	ArrayList<String> stringList;
	ArrayList<String> pictureDokumentationList;
	Customer cust;
	User user;
	int userID;
	String date,picturePath,msg, attachementPath, pictureDokuString ="";
	SignatureView drawView;
	Context context;
	String signFilePath;
	
	Spinner inngangsparti_spinner, oppholdsrom_spinner, gulvlister_spinner, vegger_spinner, lysbrytere_spinner, gulv_spinner, spiseplass_spinner, kjokkenskap_spinner,
			handvask_spinner, wc_spinner, dorflater_spinner, stelle_spinner,  under_faste_matter_spinner, soppelkasser_spinner, speil_spinner;
	EditText kontaktperson_text, inngangsparti_text, oppholdsrom_text, gulvlister_text, vegger_text, lysbrytere_text, gulv_text, spiseplass_text, kjokkenskap_text,
			handvask_text, wc_text, dorflater_text, stelle_text, under_faste_matter_text, soppelkasser_text, speil_text, temp_text, bemerkninger_text;
	TextView kontaktperson, arbeidsplassmappe, kundemappe, gulvrenhold_field, inngangsparti_field, oppholdsrom_field, lister_vegger_field, gulvlister_field, vegger_field
			,lysbrytere_field, kjokken_field, gulv_field, spiseplass_field, kjokkenskap_field, hygiene_field, handvask_field, wc_field, dorflater_field, stelle_field,
			annet_field, under_faste_matter_field, soppelkasser_field, speil_field, tillegg_field, temp_field, bemerkninger_field, fornoyd_field;
	RadioGroup radio_arbeidsplassmappe, radio_kundemappe, radio_temp, radio_fornoyd;
	RadioButton fornoyd_ja, fornoyd_nei, temp_ja, temp_nei, arbeidsplassmappe_ja, arbeidsplassmappe_nei, kundemappe_ja, kundemappe_nei;
	Button signature_button, pdf_button;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barnehage_quality_report);
		this.setTitle("Kvalitetsrapport Barnehage");
		
		context = this;		
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
	
	@Override
	public void signatureDialog() {
		final Dialog signDialog = new Dialog(context);
		signDialog.setTitle("Signér i det hvite feltet");
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
	public void sendPDF() throws Exception {
		int type = 3;
		 
		EmailGenerator gen = new EmailGenerator(this, cust, date, msg, emailList,attachementPath, type, Globals.user.getId());
		gen.sendEmail();
		finish();
		
	}

	@Override
	public void createPDF() throws IOException, DocumentException {
		int  arbeidsplassMappeSelected = radio_arbeidsplassmappe.getCheckedRadioButtonId();
		int kundemappeSelected = radio_kundemappe.getCheckedRadioButtonId();
		int tempSelected = radio_temp.getCheckedRadioButtonId();
		int fornoydSelected = radio_fornoyd.getCheckedRadioButtonId();
		
		String text = "";
		String src = Environment.getExternalStorageDirectory()
                 + "/insider_data/templates/rapport_barnehage.pdf";
         String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/barnehage_kvalitetsrapport_"+date+".pdf"; 
        
         PdfReader reader = new PdfReader(src);
         PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dst));
         
         AcroFields form = stamper.getAcroFields();
         
         form.setField("date_field", date);
         form.setField("executor_field", Globals.user.getName());
         form.setField("customer_field", cust.getName());
         form.setField("department_field", cust.getDepartment());
         form.setField("type_of_report_field", "Kvalitetsrapport Barnehage");
         form.setField("date_in_body_field", date);
         
         form.setField("participant_field", kontaktperson_text.getText().toString());
         
         if(arbeidsplassmappe_ja.isChecked()){
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	form.setField("arbeidsplassmappe_box_yes","Yes");
          }
          else{
        	  String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	 form.setField("arbeidsplassmappe_box_no", "Yes");
          }
         
         if(kundemappe_ja.isChecked()){
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	form.setField("kundemappe_box_yes","Yes");
          }
          else{
        	  String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	 form.setField("kundemappe_box_no", "Yes");
          }
         
         if(temp_ja.isChecked()){
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
          	form.setField("temp_box_yes","Yes");
           }
           else{
        	   String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
          	 form.setField("temp_box_no", "Yes");
           }
         if(fornoyd_ja.isChecked()){
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
          	form.setField("service_box_yes","Yes");
           }
           else{
        	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
          	 form.setField("service_box_no", "Yes");
           }
         
         form.setField("inngangsparti__pakledning_field",inngangsparti_spinner.getSelectedItem().toString());
         form.setField("inngangspart_pakledning_beskrivelse_field", inngangsparti_text.getText().toString());
         
         form.setField("oppholdsrom_field", oppholdsrom_spinner.getSelectedItem().toString());
         form.setField("oppholdsrom_beskrivelse_field", oppholdsrom_text.getText().toString());
         
         form.setField("gulvlister_field", gulvlister_spinner.getSelectedItem().toString());
         form.setField("gulvlister_beskrivelse_field", gulvlister_text.getText().toString());
         
         form.setField("vegger_field",vegger_spinner.getSelectedItem().toString());
         form.setField("vegger_beskrivelse_field", vegger_text.getText().toString());
         
         form.setField("lysbrytere_field", lysbrytere_spinner.getSelectedItem().toString());
         form.setField("lysbrytere_beskrivelse_field", lysbrytere_text.getText().toString());
         
         form.setField("gulv_field",gulv_spinner.getSelectedItem().toString());
         form.setField("gulv_beskrivelse_field",gulv_text.getText().toString());
         
         form.setField("spiseplass_field", spiseplass_spinner.getSelectedItem().toString());
         form.setField("spiseplass_beskrivelse_field", spiseplass_text.getText().toString());
         
         form.setField("kjokkenskap_utvendig_field", kjokkenskap_spinner.getSelectedItem().toString());
         form.setField("kjokkenskap_utvendig_beskrivelse", kjokkenskap_text.getText().toString());
         
         form.setField("håndvask_desinfisering_field", handvask_spinner.getSelectedItem().toString());
         form.setField("håndvask_desinfisering_beskrivelse_field", handvask_text.getText().toString());
         
         form.setField("wc_field", wc_spinner.getSelectedItem().toString());
         form.setField("wc_beskrivelse_field", wc_text.getText().toString());
         
         form.setField("dorflater_handtak_dispensere_field", dorflater_spinner.getSelectedItem().toString());
         form.setField("dorflater_handtak_dispensere_beskrivelse_field", dorflater_text.getText().toString());
         
         form.setField("stelleomrader_field", stelle_spinner.getSelectedItem().toString());
         form.setField("stelleomrader_beskrivelse_field", stelle_text.getText().toString());
         
         form.setField("under_faste_field", under_faste_matter_spinner.getSelectedItem().toString());
         form.setField("under_faste_beskrivelse_field", under_faste_matter_text.getText().toString());
         
         form.setField("soppelkasser_field", soppelkasser_spinner.getSelectedItem().toString());
         form.setField("soppelkasser_beskrivelse_field", soppelkasser_text.getText().toString());
         
         form.setField("speil_field", speil_spinner.getSelectedItem().toString());
         form.setField("speil_beskrivelse_field", speil_text.getText().toString());
         
         form.setField("temp_beskrivelse_field",temp_text.getText().toString());
         form.setField("bemerkninger_beskrivelse_field", bemerkninger_text.getText().toString());
         
         if(pictureList.size() != 0){
        	 addPicturesToPDF(reader, stamper);
         }
         
         for (int i = 0; i < stringList.size(); i++) {
  			pictureDokuString += stringList.get(i)+" ";
  		}
         
         if(signFilePath != ""){
        	 addSignatureToPDF(reader, stamper);
         }
         
         
         
         form.setField("bilde_field", pictureDokuString);
         
         form.setField("sign_field", cust.getDepartment()+", "+kontaktperson_text.getText().toString());
         
         stamper.setFormFlattening(true);
         stamper.close();
         reader.close();
         attachementPath = dst;
         
         for (int i = 0; i < pictureList.size(); i++) {
 			pictureList.get(i).delete();
 		}
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
	public void addPicturesToPDF(PdfReader reader, PdfStamper stamper)
			throws MalformedURLException, IOException, DocumentException {

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
		arbeidsplassmappe= (TextView) findViewById(R.id.arbeidsplassmappe);
		kundemappe = (TextView) findViewById(R.id.kundemappe);
		gulvrenhold_field = (TextView) findViewById(R.id.gulvrenhold_field);
		inngangsparti_field = (TextView) findViewById(R.id.inngangsparti_field);
		oppholdsrom_field = (TextView) findViewById(R.id.oppholdsrom_field);
		lister_vegger_field = (TextView) findViewById(R.id.lister_vegger_field);
		gulvlister_field = (TextView) findViewById(R.id.gulvlister_field);
		vegger_field = (TextView) findViewById(R.id.vegger_field);
		lysbrytere_field = (TextView) findViewById(R.id.lysbrytere_field);
		kjokken_field = (TextView) findViewById(R.id.kjokken_field);
		gulv_field = (TextView) findViewById(R.id.gulv_field);
		spiseplass_field = (TextView) findViewById(R.id.spiseplass_field);
		kjokkenskap_field = (TextView) findViewById(R.id.kjokkenskap_field);
		hygiene_field = (TextView) findViewById(R.id.hygiene_field);
		handvask_field = (TextView) findViewById(R.id.handvask_field);
		wc_field = (TextView) findViewById(R.id.wc_field);
		dorflater_field = (TextView) findViewById(R.id.dorflater_field);
		stelle_field = (TextView) findViewById(R.id.stelle_field);
		annet_field = (TextView) findViewById(R.id.annet_field);
		under_faste_matter_field = (TextView) findViewById(R.id.under_faste_matter_field);
		soppelkasser_field = (TextView) findViewById(R.id.soppelkasser_field);
		speil_field = (TextView) findViewById(R.id.speil_field);
		tillegg_field = (TextView) findViewById(R.id.tillegg_field);
		temp_field = (TextView) findViewById(R.id.temp_field);
		bemerkninger_field = (TextView) findViewById(R.id.bemerkninger_field);
		fornoyd_field = (TextView) findViewById(R.id.fornoyd_field);
		
	}

	@Override
	public void identifyCheckBox() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identifyEditText() {
		kontaktperson_text = (EditText) findViewById(R.id.kontaktperson_text);
		inngangsparti_text = (EditText) findViewById(R.id.inngangsparti_text);
		oppholdsrom_text = (EditText) findViewById(R.id.oppholdsrom_text);
		gulvlister_text = (EditText) findViewById(R.id.gulvlister_text);
		vegger_text = (EditText) findViewById(R.id.vegger_text);
		lysbrytere_text = (EditText) findViewById(R.id.lysbrytere_text);
		gulv_text = (EditText) findViewById(R.id.gulv_text);
		spiseplass_text = (EditText) findViewById(R.id.spiseplass_text);
		kjokkenskap_text = (EditText) findViewById(R.id.kjokkenskap_text);
		handvask_text  = (EditText) findViewById(R.id.handvask_text);
		wc_text = (EditText) findViewById(R.id.wc_text);
		dorflater_text  = (EditText) findViewById(R.id.dorflater_text);
		stelle_text = (EditText) findViewById(R.id.stelle_text);
		under_faste_matter_text = (EditText) findViewById(R.id.under_faste_matter_text);
		soppelkasser_text = (EditText) findViewById(R.id.soppelkasser_text);
		speil_text = (EditText) findViewById(R.id.speil_text);
		temp_text = (EditText) findViewById(R.id.temp_text);
		bemerkninger_text = (EditText) findViewById(R.id.bemerkninger_text);
		
	}

	@Override
	public void identifySpinners() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.butikk_barnehage_naringsbygg_spinner_choices, android.R.layout.simple_spinner_dropdown_item);
		inngangsparti_spinner = (Spinner) findViewById(R.id.inngangsparti_spinner);
		inngangsparti_spinner.setAdapter(adapter);
		
		oppholdsrom_spinner = (Spinner) findViewById(R.id.oppholdsrom_spinner);
		oppholdsrom_spinner.setAdapter(adapter);
		
		gulvlister_spinner = (Spinner) findViewById(R.id.gulvlister_spinner);
		gulvlister_spinner.setAdapter(adapter);
		
		vegger_spinner = (Spinner) findViewById(R.id.vegger_spinner);
		vegger_spinner.setAdapter(adapter);
		
		lysbrytere_spinner = (Spinner) findViewById(R.id.lysbrytere_spinner);
		lysbrytere_spinner.setAdapter(adapter);
		
		gulv_spinner = (Spinner) findViewById(R.id.gulv_spinner);
		gulv_spinner.setAdapter(adapter);
		
		spiseplass_spinner = (Spinner) findViewById(R.id.spiseplass_spinner);
		spiseplass_spinner.setAdapter(adapter);
		
		kjokkenskap_spinner = (Spinner) findViewById(R.id.kjokkenskap_spinner);
		kjokkenskap_spinner.setAdapter(adapter);
		
		handvask_spinner= (Spinner) findViewById(R.id.handvask_spinner);
		handvask_spinner.setAdapter(adapter);
		
		wc_spinner = (Spinner) findViewById(R.id.wc_spinner);
		wc_spinner.setAdapter(adapter);
		
		dorflater_spinner = (Spinner) findViewById(R.id.dorflater_spinner);
		dorflater_spinner.setAdapter(adapter);
		
		stelle_spinner = (Spinner) findViewById(R.id.stelle_spinner);
		stelle_spinner.setAdapter(adapter);
		
		under_faste_matter_spinner = (Spinner) findViewById(R.id.under_faste_matter_spinner);
		under_faste_matter_spinner.setAdapter(adapter);
		
		soppelkasser_spinner = (Spinner) findViewById(R.id.soppelkasser_spinner);
		soppelkasser_spinner.setAdapter(adapter);
		
		speil_spinner = (Spinner) findViewById(R.id.speil_spinner);
		speil_spinner.setAdapter(adapter);
		}

	@Override
	public void identifyRadioButtons() {
		fornoyd_ja = (RadioButton) findViewById(R.id.fornoyd_yes);
		fornoyd_nei = (RadioButton) findViewById(R.id.fornoyd_no);
		temp_ja = (RadioButton) findViewById(R.id.temp_yes);
		temp_nei= (RadioButton) findViewById(R.id.temp_no);
		arbeidsplassmappe_ja = (RadioButton) findViewById(R.id.arbeidsplassmappe_yes);
		arbeidsplassmappe_nei = (RadioButton) findViewById(R.id.arbeidsplassmappe_no);
		kundemappe_ja = (RadioButton) findViewById(R.id.kundemappe_yes);
		kundemappe_nei = (RadioButton) findViewById(R.id.kundemappe_no);
		
	}

	@Override
	public void identifyRadioGroup() {
		radio_arbeidsplassmappe = (RadioGroup) findViewById(R.id.radio_arbeidsplassmappe);
		radio_kundemappe = (RadioGroup) findViewById(R.id.radio_kundemappe);
		radio_temp = (RadioGroup) findViewById(R.id.radio_temp);
		radio_fornoyd = (RadioGroup) findViewById(R.id.radio_fornoyd);;
		
	}

	@Override
	public void identifyButtons() {
		signature_button = (Button) findViewById(R.id.sign_button);
		pdf_button = (Button) findViewById(R.id.generatePDF_button);
		
	}
}
