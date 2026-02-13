package com.selection_point.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import java.awt.Color;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.selection_point.entity.AdminSettings;
import com.selection_point.entity.Student;
import com.selection_point.entity.StudentFeeReceipt;
import com.selection_point.entity.TeacherSalary;
import com.selection_point.repository.AdminSettingsRepository;
import com.selection_point.repository.StudentFeeReceiptRepository;
import com.selection_point.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service
public class PdfService {



    @Autowired
    private SmsService smsService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Autowired
    private StudentFeeReceiptRepository receiptRepository;


    public byte[] generateSalaryReceiptPdf(TeacherSalary salary) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 80, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte canvas = writer.getDirectContentUnder();

            // ===== COLORS =====
            Color gold = new Color(212, 175, 55);
            Color blue = new Color(30, 64, 175);
            Color lightGray = new Color(245, 247, 250);

            // ===== GOLD CURVED HEADER =====
            canvas.setColorFill(gold);
            canvas.moveTo(0, 820);
            canvas.curveTo(200, 860, 350, 760, 600, 820);
            canvas.lineTo(600, 900);
            canvas.lineTo(0, 900);
            canvas.fill();

            // ===== WATERMARK =====
            PdfContentByte watermark = writer.getDirectContentUnder();
            Font wmFont = new Font(Font.HELVETICA, 60, Font.BOLD, new Color(230,230,230));
            ColumnText.showTextAligned(
                    watermark,
                    Element.ALIGN_CENTER,
                    new Phrase("SELECTION POINT", wmFont),
                    300, 400, 45
            );

            // ===== LOGO (TOP LEFT) =====
            try {
                ClassPathResource logoResource =
                        new ClassPathResource("static/logo.png");
                Image logo = Image.getInstance(
                        logoResource.getInputStream().readAllBytes()
                );
                logo.scaleAbsolute(60, 60);
                logo.setAbsolutePosition(45, 770);
                document.add(logo);
            } catch (Exception e) {
                // ignore logo if not found
            }


            // ===== FONTS =====
            Font brandFont = new Font(Font.HELVETICA, 22, Font.BOLD, blue);
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font textFont = new Font(Font.HELVETICA, 11);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            // ===== BRAND NAME =====
            Paragraph brand = new Paragraph("SELECTION POINT", brandFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            brand.setSpacingBefore(10);
            document.add(brand);

            Paragraph subtitle = new Paragraph("Salary Payment Receipt\n\n", titleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            // ===== RECEIPT META =====
            PdfPTable meta = new PdfPTable(2);
            meta.setWidthPercentage(100);
            meta.setSpacingBefore(10);

            meta.addCell(cell("Receipt No", boldFont, lightGray));
            meta.addCell(cell("SP-" + salary.getId(), textFont));

            meta.addCell(cell("Date & Time", boldFont, lightGray));
            meta.addCell(cell(
                    salary.getPaidDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                            + " | " + LocalTime.now().withNano(0),
                    textFont
            ));

            document.add(meta);
            document.add(Chunk.NEWLINE);

            // ===== TEACHER DETAILS =====
            sectionTitle("Paid To (Teacher Details)", document, headerFont);

            PdfPTable teacher = new PdfPTable(2);
            teacher.setWidthPercentage(100);

            teacher.addCell(cell("Name", boldFont, lightGray));
            teacher.addCell(cell(salary.getTeacher().getName(), textFont));

            teacher.addCell(cell("Mobile", boldFont, lightGray));
            teacher.addCell(cell(salary.getTeacher().getMobile(), textFont));

            teacher.addCell(cell("Subject", boldFont, lightGray));
            teacher.addCell(cell(salary.getTeacher().getSubject(), textFont));

            document.add(teacher);
            document.add(Chunk.NEWLINE);

            // ===== SALARY DETAILS =====
            sectionTitle("Salary Details", document, headerFont);

            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setWidthPercentage(100);

            long amount = Math.round(salary.getAmount());

            salaryTable.addCell(cell("Salary Month", boldFont, lightGray));
            salaryTable.addCell(cell(salary.getMonth(), textFont));

            salaryTable.addCell(cell("Amount Paid", boldFont, lightGray));
            salaryTable.addCell(cell("₹ " + amount, textFont));

            salaryTable.addCell(cell("Amount (In Words)", boldFont, lightGray));
            salaryTable.addCell(cell(
                    convertToWords(amount) + " Rupees Only",
                    textFont
            ));

            document.add(salaryTable);
            document.add(Chunk.NEWLINE);

            // ===== THANK YOU =====
            Paragraph thanks = new Paragraph(
                    "\nThank you for your dedication and valuable contribution 🙏",
                    boldFont
            );
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            Paragraph sign = new Paragraph(
                    "\nAuthorized By\nSelection Point",
                    boldFont
            );
            sign.setAlignment(Element.ALIGN_RIGHT);
            document.add(sign);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    // ===== HELPER METHODS =====
    private void sectionTitle(String text, Document doc, Font font) throws Exception {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(10);
        p.setSpacingAfter(5);
        doc.add(p);
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(8);
        c.setBorderColor(Color.LIGHT_GRAY);
        return c;
    }

    private PdfPCell cell(String text, Font font, Color bg) {
        PdfPCell c = cell(text, font);
        c.setBackgroundColor(bg);
        return c;
    }

    private String convertToWords(long number) {
        if (number == 0) return "Zero";

        String[] units = {"", "One","Two","Three","Four","Five","Six",
                "Seven","Eight","Nine","Ten","Eleven","Twelve","Thirteen",
                "Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen"};

        String[] tens = {"","","Twenty","Thirty","Forty","Fifty",
                "Sixty","Seventy","Eighty","Ninety"};

        if (number < 20) return units[(int) number];
        if (number < 100)
            return tens[(int) number / 10] + " " + units[(int) number % 10];
        if (number < 1000)
            return units[(int) number / 100] + " Hundred " + convertToWords(number % 100);
        if (number < 100000)
            return convertToWords(number / 1000) + " Thousand " + convertToWords(number % 1000);

        return convertToWords(number / 100000) + " Lakh " + convertToWords(number % 100000);
    }


    // Student Registration Receipt PDF Generate System

    public byte[] generateStudentReceipt(Student s) {

        Document doc = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font title = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
        Font normal = new Font(Font.HELVETICA, 11);

        doc.add(new Paragraph("SELECTION POINT\n", title));
        doc.add(new Paragraph("Student Registration Receipt\n\n"));

        doc.add(new Paragraph("Name: " + s.getName(), normal));
        doc.add(new Paragraph("Class: " + s.getStudentClass(), normal));
        doc.add(new Paragraph("Mobile: " + s.getMobile(), normal));
        doc.add(new Paragraph("Fee Paid: ₹" + s.getRegistrationFee(), normal));
        doc.add(new Paragraph("Date: " + LocalDate.now(), normal));

        doc.add(new Paragraph("\nAuthorized By\nSelection Point"));

        doc.close();
        return out.toByteArray();
    }



    //Student Main Fee Receipt Generator System

    private AdminSettings getSettings() {
        return adminSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    AdminSettings s = new AdminSettings();
                    s.setId(1L);
                    s.setUpiId("9754525446@ybl");
                    s.setInstituteName("Selection Point");
                    return adminSettingsRepository.save(s);
                });

}

    private PdfPCell metaCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell infoCell(
            String label,
            String value,
            Font labelFont,
            Font valueFont
    ) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.addElement(new Phrase(label + ": ", labelFont));
        cell.addElement(new Phrase(value, valueFont));
        return cell;
    }

    private PdfPCell signCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBorder(Rectangle.TOP);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(5);
        return cell;
    }


    public void generateAndSaveStudentFeeReceipt(
            Student student,
            double amount,
            String feeType
    ) {
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, out);
            doc.open();

            /* ================= COLORS (OpenPDF SAFE) ================= */
            Color PURPLE = new Color(79, 70, 229);
            Color ORANGE = new Color(249, 115, 22);
            Color GREEN  = new Color(22, 163, 74);
            Color LIGHT_GRAY = new Color(245, 248, 255);

            /* ================= FONTS (SAFE) ================= */
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, Color.WHITE);
            Font subFont   = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.WHITE);
            Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 11);
            Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD, GREEN);


            /* ================= HEADER ================= */
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{3, 2});

            PdfPCell left = new PdfPCell();
            left.setBackgroundColor(PURPLE);
            left.setBorder(Rectangle.NO_BORDER);
            left.setPadding(15);
            left.addElement(new Paragraph("SELECTION POINT", titleFont));
            left.addElement(new Paragraph("Quality Education for Every Student", subFont));

            PdfPCell right = new PdfPCell();
            right.setBackgroundColor(PURPLE);
            right.setBorder(Rectangle.NO_BORDER);
            right.setPadding(15);
            Paragraph contact = new Paragraph(
                    "📞 9876543210\n📧 selectionpoint@gmail.com\n📍 Main Road, Your City",
                    subFont
            );
            contact.setAlignment(Element.ALIGN_RIGHT);
            right.addElement(contact);

            header.addCell(left);
            header.addCell(right);
            doc.add(header);

            /* ================= TITLE ================= */
            PdfPCell titleCell = new PdfPCell(
                    new Phrase(
                            "FEE RECEIPT",
                            new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE)
                    )
            );
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setBackgroundColor(ORANGE);   // ORANGE = java.awt.Color
            titleCell.setPadding(8);
            titleCell.setBorder(Rectangle.NO_BORDER);

            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(40);
            titleTable.setSpacingBefore(20);
            titleTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleTable.addCell(titleCell);

            doc.add(titleTable);

            /* ================= META ================= */
            PdfPTable meta = new PdfPTable(2);
            meta.setWidthPercentage(100);
            meta.setSpacingBefore(15);

// LEFT CELL
            meta.addCell(metaCell("Date: " + LocalDate.now()));

// RIGHT CELL
            PdfPCell receiptCell =
                    metaCell("Receipt No: SP-" + System.currentTimeMillis());
            receiptCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            meta.addCell(receiptCell);

            doc.add(meta);

            /* ================= STUDENT DETAILS ================= */
            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.setSpacingBefore(15);

            details.addCell(infoCell("Student Name", student.getName(), labelFont, valueFont));
            details.addCell(infoCell("Class", student.getStudentClass(), labelFont, valueFont));
            details.addCell(infoCell("Mobile", student.getMobile(), labelFont, valueFont));
            details.addCell(infoCell("Fee Type", feeType, labelFont, valueFont));

            doc.add(details);

            /* ================= AMOUNT ================= */
            doc.add(Chunk.NEWLINE);

            Paragraph total = new Paragraph("Amount Paid: ₹" + amount, totalFont);
            doc.add(total);

            Font italicFont = new Font(Font.HELVETICA, 11, Font.ITALIC);
            Paragraph words = new Paragraph(
                    "Amount in Words: " +
                            NumberToWords.convert((long) amount) +
                            " Rupees Only",
                    italicFont
            );
            doc.add(words);


            /* ================= SIGN ================= */
            PdfPTable sign = new PdfPTable(2);
            sign.setWidthPercentage(100);
            sign.setSpacingBefore(40);

            sign.addCell(signCell("Authorised By"));
            sign.addCell(signCell("Selection Point"));

            doc.add(sign);

            /* ================= FOOTER ================= */
            PdfPCell footerCell = new PdfPCell(
                    new Phrase(
                            "Thank you for choosing Selection Point. We wish you great success!",
                            new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE)
                    )
            );
            footerCell.setBackgroundColor(PURPLE);
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerCell.setPadding(10);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPTable footer = new PdfPTable(1);
            footer.setWidthPercentage(100);
            footer.setSpacingBefore(30);
            footer.addCell(footerCell);
            doc.add(footer);

            doc.close();

            /* ================= SAVE TO DB ================= */
            StudentFeeReceipt receipt = new StudentFeeReceipt();
            receipt.setStudentId(student.getId());
            receipt.setAmount(amount);
            receipt.setFeeType(feeType);
            receipt.setCreatedAt(LocalDateTime.now());
            receipt.setReceiptNumber("SP-" + System.currentTimeMillis());
            receipt.setPdf(out.toByteArray());

            receiptRepository.save(receipt);

        } catch (Exception e) {
            throw new RuntimeException("Student receipt generation failed", e);
        }
    }




    /* 🔥 MAIN FEE / GENERAL UPI */
    public String generateUpiUrl(double amount) {
        AdminSettings s = getSettings();

        return "upi://pay"
                + "?pa=" + s.getUpiId()
                + "&pn=" + s.getInstituteName().replace(" ", "%20")
                + "&am=" + amount
                + "&cu=INR"
                + "&tn=Student-Fee";
    }

    /* 🔥 PAYMENT COMPLETE */
    @Transactional
    public Student completeFeePayment(Long studentId, double amount) {

        Student st = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        st.setMainFee(amount);
        st.setMainFeePaid(true);
        st.setPaymentDone(true);

        return studentRepository.save(st);
    }


    @Transactional
    public byte[] completeFeePaymentAndGeneratePdf(Long studentId, double amount) {

        Student st = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        st.setMainFee(amount);
        st.setMainFeePaid(true);
        st.setPaymentDone(true);

        Student saved = studentRepository.save(st);

        byte[] pdf = generateStudentReceipt(saved);

        try {
            smsService.sendSms(
                    saved.getMobile(),
                    "✅ Fee Payment Successful\n" +
                            getSettings().getInstituteName() +
                            "\nAmount: ₹" + amount
            );
        } catch (Exception ignored) {}

        return pdf;
    }

    public byte[] generateFeeReceiptPdf(Long studentId) {

        double totalPaid =
                receiptRepository.getTotalPaidAmount(studentId);

        List<StudentFeeReceipt> receipts =
                receiptRepository.findByStudentIdOrderByPaymentDateDesc(studentId);

        // yahin tum PDF generate kar rahe hoge
        // totalPaid + receipts use karo

        return new byte[0]; // demo
    }

    public class NumberToWords {

        private static final String[] units = {
                "", "One", "Two", "Three", "Four", "Five", "Six",
                "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
                "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen"
        };

        private static final String[] tens = {
                "", "", "Twenty", "Thirty", "Forty",
                "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        };

        public static String convert(long number) {

            if (number == 0) return "Zero";

            if (number < 20)
                return units[(int) number];

            if (number < 100)
                return tens[(int) number / 10] +
                        ((number % 10 != 0) ? " " + units[(int) number % 10] : "");

            if (number < 1000)
                return units[(int) number / 100] + " Hundred" +
                        ((number % 100 != 0) ? " " + convert(number % 100) : "");

            if (number < 100000)
                return convert(number / 1000) + " Thousand" +
                        ((number % 1000 != 0) ? " " + convert(number % 1000) : "");

            if (number < 10000000)
                return convert(number / 100000) + " Lakh" +
                        ((number % 100000 != 0) ? " " + convert(number % 100000) : "");

            return convert(number / 10000000) + " Crore" +
                    ((number % 10000000 != 0) ? " " + convert(number % 10000000) : "");
        }
    }

}



