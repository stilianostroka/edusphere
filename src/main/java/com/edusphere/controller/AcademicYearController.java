package com.edusphere.controller;

import com.edusphere.dto.AcademicYearRequest;
import com.edusphere.dto.AcademicYearResponse;
import com.edusphere.dto.GradingPeriodSummary;
import com.edusphere.entity.AcademicYear;
import com.edusphere.service.AcademicYearService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/academic-year")
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    @GetMapping
    public ResponseEntity<AcademicYearResponse> getCurrent(){
        return ResponseEntity.ok(toResponse(academicYearService.getActive()));
    }

    @GetMapping("/{label}")
    public ResponseEntity<AcademicYearResponse> getByLabel(@PathVariable String label){
        AcademicYear year = academicYearService.getByLabel(label)
                .orElseThrow(() -> new IllegalArgumentException("No academic year found with label " + label));
        return ResponseEntity.ok(toResponse(year));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AcademicYearResponse>> getAll(){
        return ResponseEntity.ok(academicYearService.getAll().stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/{id}/grading-periods")
    public ResponseEntity<List<GradingPeriodSummary>> getGradingPeriods(@PathVariable Long id) {
        AcademicYear year = academicYearService.getById(id);

        List<GradingPeriodSummary> periods = academicYearService.getGradingPeriods(year).stream()
                .map(p -> new GradingPeriodSummary(p.getId(), p.getSequenceNumber(), p.getStartDate(), p.getEndDate()))
                .toList();
        return ResponseEntity.ok(periods);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponse> create(@RequestBody AcademicYearRequest request){
        AcademicYear academicYear = academicYearService.createAcademicYear(request.label(), request.startDate(),
                request.endDate(), request.period1End(), request.period2End());

        return ResponseEntity.ok(toResponse(academicYear));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable Long id){
        academicYearService.setCurrentYear(academicYearService.getById(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id){
        academicYearService.deactivate(academicYearService.getById(id));
        return ResponseEntity.noContent().build();
    }

    private AcademicYearResponse toResponse(AcademicYear year) {
        return new AcademicYearResponse(
                year.getId(), year.getAcademicYear(), year.getStartDate(), year.getEndDate(), year.isActive()
        );
    }
}
