package com.edusphere.service;

import com.edusphere.entity.AcademicYear;
import com.edusphere.entity.Diploma;
import com.edusphere.entity.Student;
import com.edusphere.repository.DiplomaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class DiplomaService {
    private final DiplomaRepository diplomaRepository;
    private final StudentService studentService;
    private final AcademicYearService academicYearService;
    private final FileStorageService fileStorageService;

    public DiplomaService(DiplomaRepository diplomaRepository, StudentService studentService,
                          AcademicYearService academicYearService, FileStorageService fileStorageService) {
        this.diplomaRepository = diplomaRepository;
        this.studentService = studentService;
        this.academicYearService = academicYearService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Diploma generate(Long studentId, Long academicYearId) {
        Student student = studentService.getStudent(studentId);
        AcademicYear year = academicYearService.getById(academicYearId);
        byte[] pdf = createPdf(student, year);
        String url = fileStorageService.storeDiploma(student.getFirstName() + "-" + student.getLastName(), pdf);
        Diploma diploma = diplomaRepository.findByStudentAndAcademicYear(student, year)
                .orElseGet(() -> new Diploma(student, year, url));
        diploma.regenerate(url);
        return diplomaRepository.save(diploma);
    }

    public List<Diploma> getAll() { return diplomaRepository.findAll(); }
    public List<Diploma> getForStudent(Long studentId) { return diplomaRepository.findByStudent(studentService.getStudent(studentId)); }

    private byte[] createPdf(Student student, AcademicYear year) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 60, 60, 60, 60);
            PdfWriter.getInstance(document, output);
            document.open();
            Paragraph title = new Paragraph("DIPLOMA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\nThis certifies that", FontFactory.getFont(FontFactory.HELVETICA, 16)));
            Paragraph name = new Paragraph(student.getFirstName() + " " + student.getLastName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25));
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            Paragraph body = new Paragraph("has completed the academic year " + year.getAcademicYear() + " at our school.", FontFactory.getFont(FontFactory.HELVETICA, 16));
            body.setAlignment(Element.ALIGN_CENTER);
            body.setSpacingBefore(24);
            document.add(body);
            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Could not generate diploma PDF", e);
        }
    }
}
