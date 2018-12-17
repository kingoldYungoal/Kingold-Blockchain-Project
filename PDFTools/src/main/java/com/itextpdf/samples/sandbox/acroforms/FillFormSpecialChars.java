/*
 
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
 
*/
 
/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/20401125/overlapping-characters-in-text-field-itext-pdf
 * <p/>
 * Sometimes you need to change the font of a field.
 */
package com.itextpdf.samples.sandbox.acroforms;
 
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
 

public class FillFormSpecialChars{
    public static final String TEMPLATE = "./src/main/resources/certificate-template.pdf";
    public static final String CER = "./src/main/resources/certificate.pdf";
    public static final String VALUE = "\u011b\u0161\u010d\u0159\u017e\u00fd\u00e1\u00ed\u00e9";
 
    public static void main(String[] args) throws Exception {
        new FillFormSpecialChars().manipulatePdf(CER);
    }
 
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(TEMPLATE), new PdfWriter(CER));
 
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.setGenerateAppearance(true);
 
        PdfFont font = PdfFontFactory.createFont();
 
        form.getField("name").setValue("张三").setReadOnly(true).setFontSize(15);
        form.getField("classname").setValue("英语").setReadOnly(true).setFontSize(15);
        
        
        pdfDoc.close();
    
    }
}