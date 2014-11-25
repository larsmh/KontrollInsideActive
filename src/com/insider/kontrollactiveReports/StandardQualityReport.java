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
import android.widget.CheckBox;
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

public class StandardQualityReport extends ActionBarActivity implements ReportInterface {

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
	boolean vinduspuss_check, oppskuring_check, boning_check, hovedrenhold_check, trappevask_check, okt_frekvens_check;
	
	RadioGroup radio_arbeidsplassmappe;
	TextView arbeidsplassmappe, kontaktperson,gulv_tepper,gulv_harde,kommentar_gulv, sekundare_flater,kommentar_sekundare_flater,hygiene_sanitar, 
	kommentar_hygiene_sanitar, miljo_insider, kommentar_miljo_insider, lunsj_inside, kommentar_lunsj_inside, forskjell_etter_insider, fornoyd_med_insider,
	medarbeidere_question, generell_kommentar, anbefalt_tillegg;
	
	EditText kontaktperson_text, kommentar_gulv_text, kommentar_sekundare_flater_text, hygiene_sanitar_text, lunsj_inside_text, generell_kommentar_text, miljo_insider_text;
	
	Spinner gulv_tepper_spinner, gulv_harde_spinner, sekundare_flater_spinner, hygiene_sanitar_spinner, miljo_inside_spinner,lunsj_inside_spinner, forskjell_etter_inside_spinner,
	fornoyd_med_inside_spinner, medarbeidere_question_spinner;
	Button signature_button, pdf_button;
	
	RadioButton arbeidsplassmappe_ja, arbeidsplassmappe_nei;
	CheckBox vinduspuss, oppskuring, boning, hovedrenhold, trappevask, okt_frekvens;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standard_quality_report);
	
		this.setTitle("Standard Kvalitetsrapport");
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
//		
//		camera_button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				signatureDialog();
//			}
//		});
//		
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
	
	public void sendPDF() throws Exception{
		int type = 3;
		 
		EmailGenerator gen = new EmailGenerator(this, cust, date, msg, emailList,attachementPath, type);
		gen.sendEmail();
		finish();
	}
	
	public void createPDF() throws IOException, DocumentException{
		
		int  arbeidsplassMappeSelected = radio_arbeidsplassmappe.getCheckedRadioButtonId();
		String text = "";
		String src = Environment.getExternalStorageDirectory()
                 + "/insider_data/templates/rapport_standard.pdf";
         String dst = Environment.getExternalStorageDirectory()
                 + "/insider_data/standard_kvalitetsrapport_"+date+".pdf"; 
        
         PdfReader reader = new PdfReader(src);
         PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dst));
         
         AcroFields form = stamper.getAcroFields();
         
         form.setField("date_in_header_field", date);
         form.setField("executor_field", Globals.user.getName());
         form.setField("customer_field", cust.getName());
         form.setField("department_field", cust.getDepartment());
         form.setField("type_of_report_field", "Standard kvalitetsrapport");
         form.setField("date_in_body_field", date);
        
         
         
         if(arbeidsplassmappe_ja.isChecked()){
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
         if(text.equals("Ingen valg")){
    		 form.setField("floor_carpets_field", text);
//        	 String[] values = form.getAppearanceStates("floor_carpet_box");
        	 
         }
         
         else{
        	 form.setField("floor_carpets_field", text);
        	 form.setField("floor_carpet_box", "Yes");
         }
         
         text = gulv_harde_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("floor_hard_field", text); 
         }
         
         else{
        	 form.setField("floor_hard_field", text);
        	 form.setField("floor_hard_box", "Yes");
         }
         
         form.setField("comments_floor_field", kommentar_gulv_text.getText().toString());
         
         text = sekundare_flater_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("sec_surface_field", text); 
         }
         
         else{
        	 form.setField("sec_surface_field", text);
        	 form.setField("seq_surface_box", "Yes");
         }
         
         form.setField("comments_sec_surface_field", kommentar_sekundare_flater_text.getText().toString());
         
         text = hygiene_sanitar_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("hygene_field", text); 
         }
         
         else{
        	 form.setField("hygene_field", text);
        	 form.setField("hygene_box", "Yes");
         }
         
         form.setField("comments_hygene_field", hygiene_sanitar_text.getText().toString());
         
         text = miljo_inside_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("environment_field", text); 
         }
         
         else{
        	 form.setField("environment_field", text);
        	 form.setField("environment_box", "Yes");
         }
         
         form.setField("comments_environment_field", miljo_insider_text.getText().toString());
         
         text = lunsj_inside_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("lunsj_insider_field", text); 
         }
         
         else{
        	 form.setField("lunsj_insider_field", text);
        	 form.setField("lunsj_insider_box", "Yes");
         }
                  
         form.setField("comments_lunsj_insider_field", lunsj_inside_text.getText().toString());
         
         text = forskjell_etter_inside_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("insider_lev_field", text); 
         }
         
         else{
        	 form.setField("insider_lev_field", text);
        	 form.setField("isndier_lev_box", "Yes");
         }
         
         
         text = fornoyd_med_inside_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("happy_insider_field", text); 
         }
         
         else{
        	 form.setField("happy_insider_field", text);
        	 form.setField("happy_insider_box", "Yes");
         }
         
         text = medarbeidere_question_spinner.getSelectedItem().toString();        
         if(text.equals("Ingen valg")){
    		 form.setField("service_question_field", text); 
         }
         
         else{
        	 form.setField("service_question_field", text);
        	 form.setField("service_question_box", "Yes");
         }
         
         form.setField("general_comments_field", generell_kommentar_text.getText().toString());
         
         form.setField("vinduspuss_field", "Vinduspuss");
         form.setField("oppskuring_field", "Oppskuring");
         form.setField("boning_field", "Boning");
         form.setField("hovedrenhold_field", "Hovedrenhold");
         form.setField("trappevask_field", "Trappevask");
         form.setField("økt_frekvens_field", "økt frekvens");
         
         
         if(vinduspuss_check){
        	 form.setField("vinduspuss_box", "Yes");
         }
         
         if(oppskuring_check){
        	 form.setField("oppskuring_box", "Yes");
         }
         
         if(boning_check){
        	 form.setField("boning_box", "Yes");
         }
         
         if(hovedrenhold_check){
        	 form.setField("hovedrenhold_box", "Yes");
         }
         
         if(trappevask_check){
        	 form.setField("trappevask_box", "Yes");
         }
         
         if(okt_frekvens_check){
        	 form.setField("�kt_frekvens_box", "Yes");
         }
         
        
         
         if(pictureList.size() != 0){
        	 Log.d("!!Pic","asdasd");
        	 addPicturesToPDF(reader, stamper);
         }
         
         for (int i = 0; i < stringList.size(); i++) {
 			pictureDokuString += stringList.get(i)+" ";
 		}
          form.setField("picures_field", pictureDokuString);
         
         if(signFilePath != ""){
        	 addSignatureToPDF(reader, stamper);
         }
         
        
         form.setField("sign_field", cust.getDepartment()+", "+kontaktperson_text.getText().toString());
         
         stamper.setFormFlattening(true);
         stamper.close();
         reader.close();
         attachementPath = dst;
        
	}
	
	public void addSignatureToPDF(PdfReader reader,PdfStamper stamper) throws MalformedURLException, IOException, DocumentException{
		
		PdfContentByte overContent = stamper.getOverContent(1);
		
		Image image = Image.getInstance(signFilePath);
		image.setAbsolutePosition(292,60);
		image.scaleAbsolute(190,20);
		overContent.addImage(image);
		File sign = new File(signFilePath);
		sign.delete();
	}
	
	
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
		        
			
			
//			String img = pictureList.get(i).getAbsolutePath();
//			InputStream in = getAssets().open(img);
//			Bitmap bmp = BitmapFactory.decodeStream(in);
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//	        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//	        Image image = Image.getInstance(stream.toByteArray());
		}
		
		for (int i = 0; i < pictureList.size(); i++) {
			pictureList.get(i).delete();
		}
	}
	
	public void onCheckboxClicked(View view) {
		
		boolean checked = ((CheckBox) view).isChecked();
		
		switch (view.getId()) {
		case R.id.vinduspuss_felt:
			vinduspuss_check = true;
			break;

		case R.id.oppskuring_felt:
			oppskuring_check = true;
			break;
		case R.id.boning_felt:
			boning_check = true;
			break;
		case R.id.hovedrenhold_felt:
			hovedrenhold_check = true;
			break;
		case R.id.trappevask_felt:
			trappevask_check = true;
			break;
		case R.id.okt_frekvens_felt:
			okt_frekvens_check = true;
		default:
			break;
		}
		
	}
	
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
		kommentar_gulv = (TextView)findViewById(R.id.kommentar_gulv_harde);
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
		anbefalt_tillegg = (TextView) findViewById(R.id.anbefalt_tillegg);
	}
	
	public void identifyCheckBox(){
		vinduspuss = (CheckBox) findViewById(R.id.vinduspuss_felt);
		oppskuring = (CheckBox) findViewById(R.id.oppskuring_felt);
		boning = (CheckBox) findViewById(R.id.boning_felt);
		hovedrenhold = (CheckBox) findViewById(R.id.hovedrenhold_felt);
		trappevask = (CheckBox) findViewById(R.id.trappevask_felt);
		okt_frekvens = (CheckBox) findViewById(R.id.okt_frekvens_felt);
	}
	
	public void identifyEditText(){
		
		kontaktperson_text = (EditText) findViewById(R.id.kontaktperson_text);
		kommentar_gulv_text= (EditText) findViewById(R.id.kommentar_gulv_text);
		kommentar_sekundare_flater_text = (EditText) findViewById(R.id.kommentar_sekundare_flater_text);
		hygiene_sanitar_text = (EditText) findViewById(R.id.hygiene_sanitar_text);
		miljo_insider_text = (EditText) findViewById(R.id.miljo_insider_text);
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
		
		signature_button = (Button) findViewById(R.id.sign_button);
		pdf_button = (Button) findViewById(R.id.generatePDF_button);
		
	}
}
