//package com.bluent.interfaces.providedInterface.controller
//
//import com.bluent.interfaces.providedInterface.model.Test
//import com.bluent.interfaces.providedInterface.repository.BluentGenericRepository
//import com.bluent.interfaces.providedInterface.repository.TestRepo
//import com.bluent.interfaces.providedInterface.service.BluentGenericService
//import com.bluent.interfaces.providedInterface.service.TestService
//import org.springframework.stereotype.Controller
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import java.util.UUID
//import javax.xml.stream.events.DTD
//
//@RestController
//@RequestMapping("api/test")
//class TestController(
//    override var service: TestService
//):BluentGenericController<
//        Test,
//        Test,
//        UUID,
//        Test,
//        TestRepo,
//        TestService
//        >{
//}