package com.carrental.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rentals payment management",
        description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/payments")
public class PaymentController {


}
