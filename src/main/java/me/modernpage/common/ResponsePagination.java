package me.modernpage.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
@Slf4j
public class ResponsePagination implements ResponseBodyAdvice<Object> {

//	@Autowired
//    private PageStoreClass pageStore;
//
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		Page page = PageStoreClass.page.get();
        final StringBuilder header = new StringBuilder();
        if (page != null) {

            if (!page.isFirst()) {
                String firstPage = "<" + ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", 0)
                .replaceQueryParam("size", page.getSize()).build().encode().toUriString() + ">";
                header.append(firstPage + "; rel=\"first\"");
            }

            if (page.hasPrevious()) {
                final String prevPage = "<" + ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.previousPageable().getPageNumber())
                		.replaceQueryParam("size", page.previousPageable().getPageSize()).build().encode().toUriString() + ">";
                if(header.length() > 0)
                	header.append(", ");
                header.append(prevPage + "; rel=\"prev\"");
            }

            if (page.hasNext()) {
                final String nextPage = "<" + ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.nextPageable().getPageNumber())
                		.replaceQueryParam("size", page.nextPageable().getPageSize()).build().encode().toUriString() + ">";
                if(header.length() > 0)
                	header.append(", ");
                header.append(nextPage + "; rel=\"next\"");
            }

            if (!page.isLast()) {
                final String lastPage = "<" + ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page.getTotalPages() - 1).replaceQueryParam("size", page.getSize()).build().encode().toUriString() + ">";

                if(header.length() > 0)
                	header.append(", ");
                header.append(lastPage + "; rel= \"last\"");
            }

            if (header.length() > 0)
                response.getHeaders().add(HttpHeaders.LINK, header.toString());
            
        }
        return body;
	}

}
