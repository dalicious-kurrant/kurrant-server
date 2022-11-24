package co.dalicious.client.core.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;

public class CorrettoPageableHandlerMethodArgumentResolver
    extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {

  private final CorrettoSortHandlerMethodArgumentResolver sortResolver;

  public CorrettoPageableHandlerMethodArgumentResolver(
      CorrettoSortHandlerMethodArgumentResolver resolver) {
    super();
    this.sortResolver = resolver;
    this.setSizeParameterName("limit");
    this.setPageParameterName("offset");
  }

  @Override
  public Pageable resolveArgument(MethodParameter methodParameter,
      @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) {

    String page =
        webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
    String pageSize =
        webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));

    Sort sort =
        sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    Pageable pageable = getPageable(methodParameter, page, pageSize);

    if (sort.isSorted()) {
      return OffsetBasedPageRequest.of(pageable, sort);
    }

    return OffsetBasedPageRequest.of(pageable);
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType() == OffsetBasedPageRequest.class;
  }
}
