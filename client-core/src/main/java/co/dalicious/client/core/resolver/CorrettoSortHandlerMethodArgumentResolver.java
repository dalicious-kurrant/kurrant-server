package co.dalicious.client.core.resolver;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolverSupport;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import lombok.NonNull;

public class CorrettoSortHandlerMethodArgumentResolver
    extends SortHandlerMethodArgumentResolverSupport implements SortArgumentResolver {


  public CorrettoSortHandlerMethodArgumentResolver() {
    super();
  }

  @Override
  public Sort resolveArgument(MethodParameter methodParameter,
      @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) {

    String sortString = webRequest.getParameter(getSortParameter(methodParameter));
    return this.parseSort(sortString);
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType() == OffsetBasedPageRequest.class;
  }

  private @NonNull Sort parseSort(String sortString) {
    if (sortString == null) {
      return Sort.unsorted();
    }

    String[] sortStrings = sortString.split(",");
    List<Order> orders = new ArrayList<>(sortStrings.length);
    for (int i = 0; i < sortStrings.length; i++) {
      String currentSort = sortStrings[i];

      String[] splittedSorts = currentSort.split(":");
      if (splittedSorts.length < 1) {
        continue;
      }

      // Direction은 DESC가 기본이다.
      Direction direction = Direction.DESC;
      if (splittedSorts.length >= 2) {
        if (splittedSorts[1].toUpperCase().equals("ASC")) {
          direction = Direction.ASC;
        }
      }

      NullHandling nullHandling = NullHandling.NATIVE;
      if (splittedSorts.length >= 3) {
        if (splittedSorts[2].toUpperCase().equals("FIRST")) {
          nullHandling = NullHandling.NULLS_FIRST;
        } else if (splittedSorts[2].toUpperCase().equals("LAST")) {
          nullHandling = NullHandling.NULLS_LAST;
        }
      }

      orders.add(Order.by(splittedSorts[0]).with(direction).with(nullHandling));
    }

    return Sort.by(orders);
  }
}
