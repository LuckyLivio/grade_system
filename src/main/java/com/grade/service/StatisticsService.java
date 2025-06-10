package com.grade.service;

import com.grade.entity.UserRole;
import com.grade.repository.GradeRepository;
import com.grade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Map<String, Object> getStatistics(String semester, Long departmentId, Long majorId, Long classId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 基础统计
            stats.put("totalStudents", userRepository.countByRole(UserRole.STUDENT));
            stats.put("totalTeachers", userRepository.countByRole(UserRole.TEACHER));
            stats.put("totalGrades", gradeRepository.count());
            
            // 成绩统计 - 只有在有成绩数据时才查询
            long gradeCount = gradeRepository.count();
            if (semester != null && gradeCount > 0) {
                stats.put("averageScore", gradeRepository.findAverageScoreBySemester(semester).orElse(BigDecimal.ZERO));
                stats.put("maxScore", gradeRepository.findMaxScoreBySemester(semester).orElse(BigDecimal.ZERO));
                stats.put("minScore", gradeRepository.findMinScoreBySemester(semester).orElse(BigDecimal.ZERO));
                stats.put("passRate", gradeRepository.findPassRateBySemester(semester, new BigDecimal("60")).orElse(BigDecimal.ZERO));
            } else {
                stats.put("averageScore", BigDecimal.ZERO);
                stats.put("maxScore", BigDecimal.ZERO);
                stats.put("minScore", BigDecimal.ZERO);
                stats.put("passRate", BigDecimal.ZERO);
            }
            
            // 按院系统计
            if (departmentId != null) {
                try {
                    if (gradeCount > 0) {
                        stats.put("departmentAverageScore", gradeRepository.findAverageScoreByDepartment(departmentId).orElse(BigDecimal.ZERO));
                    } else {
                        stats.put("departmentAverageScore", BigDecimal.ZERO);
                    }
                    stats.put("departmentStudentCount", userRepository.countStudentsByDepartment(departmentId));
                } catch (Exception e) {
                    stats.put("departmentAverageScore", BigDecimal.ZERO);
                    stats.put("departmentStudentCount", 0L);
                }
            }
            
            // 按专业统计
            if (majorId != null) {
                try {
                    if (gradeCount > 0) {
                        stats.put("majorAverageScore", gradeRepository.findAverageScoreByMajor(majorId).orElse(BigDecimal.ZERO));
                    } else {
                        stats.put("majorAverageScore", BigDecimal.ZERO);
                    }
                    stats.put("majorStudentCount", userRepository.countStudentsByMajor(majorId));
                } catch (Exception e) {
                    stats.put("majorAverageScore", BigDecimal.ZERO);
                    stats.put("majorStudentCount", 0L);
                }
            }
            
            // 按班级统计
            if (classId != null) {
                try {
                    if (gradeCount > 0) {
                        stats.put("classAverageScore", gradeRepository.findAverageScoreByClass(classId).orElse(BigDecimal.ZERO));
                    } else {
                        stats.put("classAverageScore", BigDecimal.ZERO);
                    }
                    stats.put("classStudentCount", userRepository.countStudentsByClass(classId));
                } catch (Exception e) {
                    stats.put("classAverageScore", BigDecimal.ZERO);
                    stats.put("classStudentCount", 0L);
                }
            }
            
        } catch (Exception e) {
            // 如果出现任何错误，返回默认值
            stats.put("totalStudents", 0L);
            stats.put("totalTeachers", 0L);
            stats.put("totalGrades", 0L);
            stats.put("averageScore", BigDecimal.ZERO);
            stats.put("maxScore", BigDecimal.ZERO);
            stats.put("minScore", BigDecimal.ZERO);
            stats.put("passRate", BigDecimal.ZERO);
        }
        
        return stats;
    }
}