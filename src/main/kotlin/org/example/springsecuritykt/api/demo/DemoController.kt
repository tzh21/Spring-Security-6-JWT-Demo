package org.example.springsecuritykt.api.demo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/demo")
class DemoController {

    @GetMapping
    fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello from /api/v1/demo!")
    }

}