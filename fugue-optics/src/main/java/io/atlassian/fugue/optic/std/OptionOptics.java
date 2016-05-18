package io.atlassian.fugue.optic.std;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.PPrism;
import io.atlassian.fugue.optic.Prism;

import java.util.Optional;

import static io.atlassian.fugue.Either.left;
import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.optic.PPrism.pPrism;

public class OptionOptics {

  private OptionOptics() {}

  public static <A, B> PPrism<Option<A>, Option<B>, A, B> pSome() {
    return pPrism(oa -> oa.fold(() -> left(none()), Either::right), Option::some);
  }

  public static <A> Prism<Option<A>, A> some() {
    return new Prism<>(pSome());
  }

  public static <A> Iso<Option<A>, Optional<A>> optionToOptional() {
    return Iso.iso(Option::toOptional, Option::fromOptional);
  }

}
