package com.insider.kontrollactiveReports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.insider.kontrollactive.EmailGenerator;
import com.insider.kontrollactive.R;
import com.insider.kontrollactiveModel.Date;
import com.insider.kontrollactiveModel.Globals;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
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
