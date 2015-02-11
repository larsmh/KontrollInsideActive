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

public class EiendomsdriftQualityReport extends ActionBarActivity implements ReportInterface{

	static final int REQUEST_TAKE_PHOTO = 1;
	ArrayList<File> pictureList;
	ArrayList<Email> emailList;
	ArrayList<String> stringList;
	ArrayList<String> pictureDokumentationList;
	Customer cust;
	User user;
	String date,picturePath,msg, attachementPath, pictureDokuString ="";
	SignatureView drawView;
	Context context;
	String signFilePath;
	
	EditText kontaktperson_text, gulv_repos_text, trapper_text, heisgulv_text, heishus_text, glass_inngangsdor_text, flekkfjerning_text, postkasser_text, gelender_rekkverk_text
	, toalett_text, diverse_text, temp_text;
	
	Spinner gulv_repos_spinner, trapper_spinner, heisgulv_spinner, heishus_spinner, glass_inngangsdor_spinner, flekkfjerning_spinner, postkasser_spinner, gelender_rekkverk_spinner,
	toalett_spinner, diverse_spinner;
	
	RadioGroup radio_arbeidsplassmappe, radio_kundemappe, radio_temp;
	RadioButton  temp_ja, temp_nei, arbeidsplassmappe_ja, arbeidsplassmappe_nei, kundemappe_ja, kundemappe_nei;		
	Button signature_button, pdf_button;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eiendomsdrift_quality_report);
		this.setTitle("Kvalitetsrapport Eiendromsdrift");
		
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
				Toast.makeText(v.getContext(), "Rapport laget!", Toast.LENGTH_LONG)
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
		
		String text = "";
		String src = Environment.getExternalStorageDirectory()
                 + "/insider_data/templates/rapport_eiendomsdrift.pdf";
         String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/eiendomsdrift_kvalitetsrapport_"+date+".pdf"; 
        
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
         
         
         form.setField("gulv_field",gulv_repos_spinner.getSelectedItem().toString());
         form.setField("gulv_beskrivelse_field", gulv_repos_text.getText().toString());
         
         form.setField("trapper_field",trapper_spinner.getSelectedItem().toString());
         form.setField("trapper_beskrivelse_field", trapper_text.getText().toString());
         
         form.setField("heisgulv_field",heisgulv_spinner.getSelectedItem().toString());
         form.setField("heisgulv_beskrivelse_field", heisgulv_text.getText().toString());
         
         form.setField("heishus_field",heishus_spinner.getSelectedItem().toString());
         form.setField("heishus_beskrivelse_field", heishus_text.getText().toString());
         
         form.setField("glass_field",glass_inngangsdor_spinner.getSelectedItem().toString());
         form.setField("glass_beskrivelse_field", glass_inngangsdor_text.getText().toString());
         
         form.setField("flekkfjerning_field",flekkfjerning_spinner.getSelectedItem().toString());
         form.setField("flekkfjerning_beskrivelse_field", flekkfjerning_text.getText().toString());
         
         form.setField("postkasser_field",postkasser_spinner.getSelectedItem().toString());
         form.setField("postkasser_beskrivelse_field", postkasser_text.getText().toString());
         
         form.setField("gelender_field",gelender_rekkverk_spinner.getSelectedItem().toString());
         form.setField("gelender_beskrivelse_field", gelender_rekkverk_text.getText().toString());
         
         form.setField("toalett_field",toalett_spinner.getSelectedItem().toString());
         form.setField("toalett_beskrivelse_field", toalett_text.getText().toString());
         
         form.setField("postkasser_field",postkasser_spinner.getSelectedItem().toString());
         form.setField("postkasser_beskrivelse_field", postkasser_text.getText().toString());
		
         form.setField("temp_beskrivelse_field", temp_text.getText().toString());
         form.setField("bemerkninger_field", diverse_text.getText().toString());
         
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identifyCheckBox() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void identifyEditText() {
		kontaktperson_text = (EditText) findViewById(R.id.kontaktperson_text);
		gulv_repos_text = (EditText) findViewById(R.id.gulv_repos_text);
		trapper_text = (EditText) findViewById(R.id.trapper_text);
		heisgulv_text = (EditText) findViewById(R.id.heisgulv_text);
		heishus_text = (EditText) findViewById(R.id.heishus_text);
		glass_inngangsdor_text = (EditText) findViewById(R.id.glass_inngangsdor_text);
		flekkfjerning_text = (EditText) findViewById(R.id.flekkfjerning_text);
		postkasser_text = (EditText) findViewById(R.id.postkasser_text);
		gelender_rekkverk_text = (EditText) findViewById(R.id.gelender_rekkverk_text);
		toalett_text = (EditText) findViewById(R.id.toalett_text);
		diverse_text = (EditText) findViewById(R.id.diverse_text);
		temp_text = (EditText) findViewById(R.id.temp_text);
		
	}

	@Override
	public void identifySpinners() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.extended_spinner_choices, android.R.layout.simple_spinner_dropdown_item);
		
		gulv_repos_spinner = (Spinner) findViewById(R.id.gulv_repos_spinner);
		gulv_repos_spinner.setAdapter(adapter);
		
		trapper_spinner = (Spinner) findViewById(R.id.trapper_spinner);
		trapper_spinner.setAdapter(adapter);
		
		heisgulv_spinner = (Spinner) findViewById(R.id.heisgulv_spinner);
		heisgulv_spinner.setAdapter(adapter);
		
		heishus_spinner = (Spinner) findViewById(R.id.heishus_spinner);
		heishus_spinner.setAdapter(adapter);
		
		glass_inngangsdor_spinner = (Spinner) findViewById(R.id.glass_inngangsdor_spinner);
		glass_inngangsdor_spinner.setAdapter(adapter);
		
		flekkfjerning_spinner = (Spinner) findViewById(R.id.flekkfjerning_spinner);
		flekkfjerning_spinner.setAdapter(adapter);
		
		postkasser_spinner = (Spinner) findViewById(R.id.postkasser_spinner);
		postkasser_spinner.setAdapter(adapter);
		
		gelender_rekkverk_spinner = (Spinner) findViewById(R.id.gelender_rekkverk_spinner);
		gelender_rekkverk_spinner.setAdapter(adapter);
		
		toalett_spinner = (Spinner) findViewById(R.id.toalett_spinner);
		toalett_spinner.setAdapter(adapter);
	}

	@Override
	public void identifyRadioButtons() {
		temp_ja = (RadioButton) findViewById(R.id.temp_yes);
		temp_nei = (RadioButton) findViewById(R.id.temp_no);
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
		
	}

	@Override
	public void identifyButtons() {
		signature_button = (Button) findViewById(R.id.sign_button);
		pdf_button = (Button) findViewById(R.id.generatePDF_button);
		
	}
}
