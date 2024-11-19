package com.nk.user_service.filter;


//@Component
//@Slf4j
//public class RoleValidationFilter extends OncePerRequestFilter {
//    private final ErrorMetadataLoader errorMetadataLoader;
//    private final ObjectMapper objectMapper;
//
//    public RoleValidationFilter(ErrorMetadataLoader errorMetadataLoader, ObjectMapper objectMapper) {
//        this.errorMetadataLoader = errorMetadataLoader;
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        String rolesHeader = request.getHeader("X-Roles");
//
//        if (rolesHeader == null || !rolesHeader.contains("USER")) {
//            ErrorMetadata errorMetadata = errorMetadataLoader.getErrorMetadata("FORBIDDEN");
//            ResponseStatus responseStatus = new ResponseStatus(errorMetadata);
//            ResponseDto<String> responseDto = new ResponseDto<>(responseStatus);
//
//
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            // Convert ResponseDto to JSON and write it to the response output stream
//            objectMapper.writeValue(response.getWriter(), responseDto);
//            response.getWriter().flush();
//            log.info("Role validation failed");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
