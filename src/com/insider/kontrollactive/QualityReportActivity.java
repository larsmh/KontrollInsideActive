package com.insider.kontrollactive;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import com.insider.kontrollactiveModel.Customer;
import com.insider.kontrollactiveModel.Globals;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QualityReportActivity extends ActionBarActivity {
	Button makePDF;
	File dir;
	File file;
	String date, msg;
	Document document;
	Customer cust;
	ArrayList<Email> emailList;
	int code, choiceNumber;
	int type = 3;
	String attachement;
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quality_report);
		
		Intent intent = getIntent();
		choiceNumber = Integer.parseInt(intent.getStringExtra("choice"));
		
		switch (choiceNumber) {
		case 0:
			setContentView(R.layout.activity_quality_report);
			break;

		default:
			break;
		}
		
		emailList = Globals.emaiList;
		date = new com.insider.kontrollactiveModel.Date().getDate();
		msg = "k";
		
		makePDF = (Button) findViewById(R.id.makePDF);
		
		Bundle b = intent.getExtras();
		cust = b.getParcelable("customerObject");
		Toast.makeText(this, cust.getEmail(), Toast.LENGTH_LONG).show();
		
		String[] choices = getResources().getStringArray(R.array.quality_report_types);
		
		this.setTitle(choices[choiceNumber]);

		dir = getAlbumStorageDir(this, "insider_data");
		file = new File(dir.getAbsolutePath()+"/"+cust.getEmail()+".pdf");
	
		makePDF.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					create(v);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void create(View view) throws Exception{

		
		Log.d("!!pdf", dir.getAbsolutePath());
		try {
	      Document document = new Document();
	      PdfWriter.getInstance(document, new FileOutputStream(file));
	      document.open();
	      addMetaData(document);
	      addTitlePage(document);
	      addContent(document);
	      document.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	   }
	
		EmailGenerator gen = new EmailGenerator(this, cust, date,msg, emailList,attachement, type);
		gen.sendEmail();
	}
	
	
	private static void addMetaData(Document document) {
	    document.addTitle("My first PDF");
	    document.addSubject("Using iText");
	    document.addKeywords("Java, PDF, iText");
	    document.addAuthor("Lars Vogel");
	    document.addCreator("Lars Vogel");
	  }

	  private static void addTitlePage(Document document)
	      throws DocumentException {
	    Paragraph preface = new Paragraph();
	    // We add one empty line
	    addEmptyLine(preface, 1);
	    // Lets write a big header
	    preface.add(new Paragraph("V�r f�rste pdf!", catFont));

	    addEmptyLine(preface, 1);
	    // Will create: Report generated by: _name, _date
	    preface.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        smallBold));
	    addEmptyLine(preface, 3);
	    preface.add(new Paragraph("This document describes something which is very important ",
	        smallBold));

	    addEmptyLine(preface, 8);

	    preface.add(new Paragraph("This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
	        redFont));

	    document.add(preface);
	    // Start a new page
	    document.newPage();
	  }

	  private static void addContent(Document document) throws DocumentException {
	    Anchor anchor = new Anchor("First Chapter", catFont);
	    anchor.setName("First Chapter");

	    // Second parameter is the number of the chapter
	    Chapter catPart = new Chapter(new Paragraph(anchor), 1);

	    Paragraph subPara = new Paragraph("Subcategory 1", subFont);
	    Section subCatPart = catPart.addSection(subPara);
	    subCatPart.add(new Paragraph("Hello"));

	    subPara = new Paragraph("Subcategory 2", subFont);
	    subCatPart = catPart.addSection(subPara);
	    subCatPart.add(new Paragraph("Paragraph 1"));
	    subCatPart.add(new Paragraph("Paragraph 2"));
	    subCatPart.add(new Paragraph("Paragraph 3"));

	    // add a list
	    createList(subCatPart);
	    Paragraph paragraph = new Paragraph();
	    addEmptyLine(paragraph, 5);
	    subCatPart.add(paragraph);

	    // add a table
	    createTable(subCatPart);

	    // now add all this to the document
	    document.add(catPart);

	    // Next section
	    anchor = new Anchor("Second Chapter", catFont);
	    anchor.setName("Second Chapter");

	    // Second parameter is the number of the chapter
	    catPart = new Chapter(new Paragraph(anchor), 1);

	    subPara = new Paragraph("Subcategory", subFont);
	    subCatPart = catPart.addSection(subPara);
	    subCatPart.add(new Paragraph("This is a very important message"));

	    // now add all this to the document
	    document.add(catPart);

	  }

	  private static void createTable(Section subCatPart)
	      throws BadElementException {
	    PdfPTable table = new PdfPTable(3);

	    // t.setBorderColor(BaseColor.GRAY);
	    // t.setPadding(4);
	    // t.setSpacing(4);
	    // t.setBorderWidth(1);

	    PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(c1);

	    c1 = new PdfPCell(new Phrase("Table Header 2"));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(c1);

	    c1 = new PdfPCell(new Phrase("Table Header 3"));
	    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(c1);
	    table.setHeaderRows(1);

	    table.addCell("1.0");
	    table.addCell("1.1");
	    table.addCell("1.2");
	    table.addCell("2.1");
	    table.addCell("2.2");
	    table.addCell("2.3");

	    subCatPart.add(table);

	  }

	  private static void createList(Section subCatPart) {
	    List list = new List(true, false, 10);
	    list.add(new ListItem("First point"));
	    list.add(new ListItem("Second point"));
	    list.add(new ListItem("Third point"));
	    subCatPart.add(list);
	  }

	  private static void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	  }
	
		public boolean isExternalStorageWritable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    }
		    return false;
		}

		/* Checks if external storage is available to at least read */
		public boolean isExternalStorageReadable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state) ||
		        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		        return true;
		    }
		    return false;
		}
		
		public File getAlbumStorageDir(Context context, String albumName) {
		    // Get the directory for the user's public pictures directory. 
		    File file = new File(Environment.getExternalStorageDirectory(), albumName);
		    if (!file.mkdirs()) {
		        Log.e("LOG_TAG", "Directory not created");
		    }
		    return file;
		}
  	
		
}


