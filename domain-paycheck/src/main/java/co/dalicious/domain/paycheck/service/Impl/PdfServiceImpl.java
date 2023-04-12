package co.dalicious.domain.paycheck.service.Impl;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.svg.converter.SvgConverter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class PdfServiceImpl {

//    public String excelToHtml(Workbook workbook) throws Exception {
//            ExcelToHtmlConverter converter = new ExcelToHtmlConverter();
//            converter.processWorkbook((XSSFWorkbook) workbook);
//
//            StringWriter writer = new StringWriter();
//            org.apache.poi.hssf.converter.ExcelToHtmlUtils.transform(converter.getDocument(), writer);
//
//            return writer.toString();
//
//    }
//
//    public void htmlToPdf(String html, String pdfFilePath) throws FileNotFoundException {
//        try {
//            PdfWriter writer = new PdfWriter(pdfFilePath);
//            PdfDocument pdfDoc = new PdfDocument(writer);
//            com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc);
//
//            HtmlConverter.convertToPdf(html, pdfDoc);
//
//            doc.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
