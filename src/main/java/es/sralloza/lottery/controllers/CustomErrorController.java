// package es.sralloza.lottery.controllers;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;

// @ControllerAdvice
// @RestController
// public class CustomErrorController {

//     private class JsonResponse {
//         String message;

//         public JsonResponse(String message) {
//             super();
//             this.message = message;
//         }
//     }

//     @ExceptionHandler(value = Exception.class)
//     public ResponseEntity<JsonResponse> handleException(Exception e) {
//         HttpStatus code;
//         String message;

//         try {
//             ResponseStatus ann = e.getClass().getAnnotationsByType(ResponseStatus.class)[0];
//             code = ann.code();
//             message = e.getMessage();
//         } catch (IndexOutOfBoundsException e1) {
//             e.printStackTrace();
//             code = HttpStatus.INTERNAL_SERVER_ERROR;
//             message = "Unexpected server error";
//         }

//         return new ResponseEntity<JsonResponse>(new JsonResponse(message), code);
//     }

// }
