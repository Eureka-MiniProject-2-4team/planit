package com.eureka.mp2.team4.planit.todo.personal.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.service.PersonalTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo/personal")
@RequiredArgsConstructor
public class PersonalTodoController {

    private final PersonalTodoService personalTodoService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable String id,
                                              @RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(personalTodoService.delete(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(personalTodoService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllByUser(@RequestParam String userId) {
        return ResponseEntity.ok(personalTodoService.getAllByUser(userId));
    }
}
