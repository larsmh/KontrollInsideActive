package com.insider.kontrollactiveReports;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public interface ReportInterface {

	public void onCreate(Bundle savedInstanceState);
	
	public boolean onCreateOptionsMenu(Menu menu);
	public boolean onOptionsItemSelected(MenuItem item);
	public void signatureDialog();
	public void sendPDF()  throws Exception;
	public void createPDF()throws IOException, DocumentException;
	public void addSignatureToPDF(PdfReader reader,PdfStamper stamper) throws MalformedURLException, IOException, DocumentException;
	public void addPicturesToPDF(PdfReader reader, PdfStamper stamper) throws MalformedURLException, IOException, DocumentException;
	public void onCheckboxClicked(View view);
	public void dispatchTakePictureIntent();
	public File createImageFile() throws IOException;
	public void identifyTextView();
	public void identifyCheckBox();
	public void identifyEditText();
	public void identifySpinners();
	
	public void identifyRadioButtons();
	
	public void identifyRadioGroup();
	
	public void identifyButtons();
	
	
}
