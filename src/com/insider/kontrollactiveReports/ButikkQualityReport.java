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

public class ButikkQualityReport extends ActionBarActivity implements ReportInterface {

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
	
	EditText kontaktperson_text, harde_text, gulvlister_text, hyller_text, panteomrade_text, kjole_text, kasseområde_text, handvask_desinfisering_text, wc_dispensere_text,
	under_faste_matter_text, glass_inngangsparti_text, annet1_text, annet2_text, annet3_text, temp_text, bemerkninger_text;
	
	RadioGroup radio_arbeidsplassmappe, radio_kundemappe, radio_temp;
	
	RadioButton arbeidsplassmappe_yes, arbeidsplassmappe_no, kundemappe_yes, kundemappe_no, temp_yes, temp_no;
	
	Spinner harde_spinner, gulvlister_spinner, hyller_spinner, panteomrade_spinner, kjole_spinner, kasseomrade_spinner, handvask_desinfisering_spinner, wc_dispensere_spinner
	,under_faste_matter_spinner, glass_inngangsparti_spinner, annet1_spinner, annet2_spinner, annet3_spinner;
	
	Button pdf_button, signature_button;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.butikk_quality_report);
		
		this.setTitle("Kvalitetsrapport Butikk");
		
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
                 + "/insider_data/templates/rapport_butikk.pdf";
        String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/butikk_kvalitetsrapport_"+date+".pdf"; 
        
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dst));
         
        AcroFields form = stamper.getAcroFields();
		
        form.setField("date_field", date);
        form.setField("executor_field", Globals.user.getName());
        form.setField("customer_field", cust.getName());
        form.setField("department_field", cust.getDepartment());
        form.setField("type_of_report_field", "kvalitetsrapport Butikk");
        form.setField("date_in_body_field", date);
        form.setField("contact_field", kontaktperson_text.getText().toString());
       
        
        
        if(arbeidsplassmappe_yes.isChecked()){
       	String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
       	form.setField("arbeidsplassmappe_box_yes","Yes");
        }
        else{
       	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_no");
       	 form.setField("arbeidsplassmappe_box_no", "Yes");
        }

        if(kundemappe_yes.isChecked()){
       	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
        	form.setField("kundemappe_box_yes","Yes");
         }
         else{
       	  String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
        	 form.setField("kundemappe_box_no", "Yes");
         }
        
        if(temp_yes.isChecked()){
       	 String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	form.setField("temp_box_yes","Yes");
          }
          else{
       	   String[] values = form.getAppearanceStates("arbeidsplassmappe_box_yes");
         	 form.setField("temp_box_no", "Yes");
          }
        
        form.setField("harde_field",harde_spinner.getSelectedItem().toString());
        form.setField("harde_beskrivelse_field", harde_text.getText().toString());
        
        form.setField("gulvlister_field",gulvlister_spinner.getSelectedItem().toString());
        form.setField("gulvlister_beskrivelse_field", gulvlister_text.getText().toString());
        
        form.setField("hyller_field",hyller_spinner.getSelectedItem().toString());
        form.setField("hyller_beskrivelse_field", hyller_text.getText().toString());
        
        form.setField("panteomrade_field",panteomrade_spinner.getSelectedItem().toString());
        form.setField("panteomrade_beskrivelse_field", panteomrade_text.getText().toString());
        
        form.setField("kjolelager_field",kjole_spinner.getSelectedItem().toString());
        form.setField("kjolelager_beskrivelse_field", kjole_text.getText().toString());
        
        form.setField("kasseomrade_field",kasseomrade_spinner.getSelectedItem().toString());
        form.setField("kasseomrade_beskrivelse_field", kasseområde_text.getText().toString());
        
        form.setField("handvask_field",handvask_desinfisering_spinner.getSelectedItem().toString());
        form.setField("handvask_beskrivelse_field", handvask_desinfisering_text.getText().toString());
        
        form.setField("wc_field",wc_dispensere_spinner.getSelectedItem().toString());
        form.setField("wc_beskrivelse_field", wc_dispensere_text.getText().toString());
        
        form.setField("faste_matter_field",under_faste_matter_spinner.getSelectedItem().toString());
        form.setField("faste_matter_beskrivelse_field", under_faste_matter_text.getText().toString());
        
        form.setField("glass_field",glass_inngangsparti_spinner.getSelectedItem().toString());
        form.setField("glass_beskrivelse_field", glass_inngangsparti_text.getText().toString());
        
        form.setField("annet1_field",annet1_spinner.getSelectedItem().toString());
        form.setField("annet1_beskrivelse_field", annet1_text.getText().toString());
        
        form.setField("annet2_field",annet2_spinner.getSelectedItem().toString());
        form.setField("annet2_beskrivelse_field", annet2_text.getText().toString());
        
        form.setField("annet3_field",annet3_spinner.getSelectedItem().toString());
        form.setField("annet3_beskrivelse_field", annet3_text.getText().toString());
        
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
		image.setAbsolutePosition(292,108);
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
		harde_text = (EditText) findViewById(R.id.harde_text);
		gulvlister_text = (EditText) findViewById(R.id.gulvlister_text);
		hyller_text = (EditText) findViewById(R.id.hyller_text);
		panteomrade_text = (EditText) findViewById(R.id.panteomrade_text);
		kjole_text = (EditText) findViewById(R.id.kjole_text);
		kasseområde_text = (EditText) findViewById(R.id.kasseomrade_text);
		handvask_desinfisering_text = (EditText) findViewById(R.id.handvask_desinfisering_text);
		wc_dispensere_text = (EditText) findViewById(R.id.wc_dispensere_text);
		under_faste_matter_text = (EditText) findViewById(R.id.under_faste_matter_text);
		glass_inngangsparti_text = (EditText) findViewById(R.id.glass_inngangsparti_text);
		annet1_text = (EditText) findViewById(R.id.annet1_text);
		annet2_text = (EditText) findViewById(R.id.annet2_text);
		annet3_text = (EditText) findViewById(R.id.annet3_text);
		temp_text = (EditText) findViewById(R.id.temp_text);
		bemerkninger_text = (EditText) findViewById(R.id.bemerkninger_text);
		
	}

	@Override
	public void identifySpinners() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,  R.array.butikk_barnehage_naringsbygg_spinner_choices, android.R.layout.simple_spinner_dropdown_item); 
		harde_spinner = (Spinner) findViewById(R.id.harde_spinner);
		harde_spinner.setAdapter(adapter);
		
		gulvlister_spinner = (Spinner) findViewById(R.id.gulvlister_spinner);
		gulvlister_spinner.setAdapter(adapter);
		
		hyller_spinner = (Spinner) findViewById(R.id.hyller_spinner);
		hyller_spinner.setAdapter(adapter);
		
		panteomrade_spinner = (Spinner) findViewById(R.id.panteomrade_spinner);
		panteomrade_spinner.setAdapter(adapter);
		
		kjole_spinner = (Spinner) findViewById(R.id.kjole_spinner);
		kjole_spinner.setAdapter(adapter);
		
		kasseomrade_spinner = (Spinner) findViewById(R.id.kasseomrade_spinner);
		kasseomrade_spinner.setAdapter(adapter);
		
		handvask_desinfisering_spinner = (Spinner) findViewById(R.id.handvask_desinfisering_spinner);
		handvask_desinfisering_spinner.setAdapter(adapter);
		
		wc_dispensere_spinner = (Spinner) findViewById(R.id.wc_dispensere_spinner);
		wc_dispensere_spinner.setAdapter(adapter);
		
		under_faste_matter_spinner = (Spinner) findViewById(R.id.under_faste_matter_spinner);
		under_faste_matter_spinner.setAdapter(adapter);
		
		glass_inngangsparti_spinner = (Spinner) findViewById(R.id.glass_inngangsparti_spinner);
		glass_inngangsparti_spinner.setAdapter(adapter);
		
		annet1_spinner = (Spinner) findViewById(R.id.annet1_spinner);
		annet1_spinner.setAdapter(adapter);
		
		annet2_spinner = (Spinner) findViewById(R.id.annet2_spinner);
		annet2_spinner.setAdapter(adapter);
		
		annet3_spinner = (Spinner) findViewById(R.id.annet3_spinner);
		annet3_spinner.setAdapter(adapter);
	}

	@Override
	public void identifyRadioButtons() {
		arbeidsplassmappe_yes = (RadioButton) findViewById(R.id.arbeidsplassmappe_yes);
		arbeidsplassmappe_no = (RadioButton) findViewById(R.id.arbeidsplassmappe_no);
		kundemappe_yes = (RadioButton) findViewById(R.id.kundemappe_yes);
		kundemappe_no = (RadioButton) findViewById(R.id.kundemappe_no);
		temp_yes = (RadioButton) findViewById(R.id.temp_yes);
		temp_no = (RadioButton) findViewById(R.id.temp_no);
		
	}

	@Override
	public void identifyRadioGroup() {
		radio_arbeidsplassmappe = (RadioGroup) findViewById(R.id.radio_arbeidsplassmappe);
		radio_kundemappe = (RadioGroup) findViewById(R.id.radio_kundemappe);
		radio_temp = (RadioGroup) findViewById(R.id.radio_temp);
		
	}

	@Override
	public void identifyButtons() {

		pdf_button = (Button) findViewById(R.id.generatePDF_button);
		signature_button = (Button) findViewById(R.id.sign_button);
	}

}
