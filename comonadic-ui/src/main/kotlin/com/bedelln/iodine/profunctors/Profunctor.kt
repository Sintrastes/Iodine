
interface Profunctor<P> {
    fun <A,B,X> Hk2<P,A,B>.lmap(f: (X) -> A): Hk2<P,X,B>
    fun <A,B,X> Hk2<P,A,B>.rmap(f: (B) -> X): Hk2<P,A,X>
}

interface Choice<P> {

}

interface Cochoice<P> {
    fun <A,B,D> unleft(x: Hk2<P, Either<A, D>, Either<B,D>>): Hk2<P,A,B>
}

interface Strong<P> {

}

interface Costring<P> {

}

interface Coprism<S,T,A,B> {
    <P> fun CoChoice<P>.transform(x: Hk2<P,A,B>): Hk2<P,S,T>
}

/** Helper function to build a Coprism. */
fun <S,T,A,B> coprism(to: (S) -> A, from: (B) -> Either<A, T>): Coprism<S,T,A,B> {
    TODO()
}

// Coprism<A, A, Pair<E?,A>, Pair<E?,A>>
//     (A) -> Pair<E?, A>
//     (Pair<E?, A>) -> Either<Pair<E?,A>, A>
