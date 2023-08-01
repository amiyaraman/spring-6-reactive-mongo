package com.amiya.reactivemongo.web.fn;

import com.amiya.reactivemongo.model.BeerDTO;
import com.amiya.reactivemongo.service.BeerService;
import org.springframework.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BeerHandler {

    private final BeerService beerService;

    private final Validator validator;

    private void validate(BeerDTO beerDTO){
        Errors errors = new BeanPropertyBindingResult(beerDTO, "beerDto");

        validator.validate(beerDTO, errors);

        if (errors.hasErrors()){
            throw new ServerWebInputException(errors.toString());
        }
    }

    public Mono<ServerResponse> helloWorld(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.just("hello"), String.class);
    }

    public Mono<ServerResponse> listBeer(ServerRequest request){
        Flux<BeerDTO> flux;

        if (request.queryParam("beerStyle").isPresent()){
            flux = beerService.findByBeerStyle(request.queryParam("beerStyle").get());
        } else {
            flux = beerService.listBeers();
        }

        return ServerResponse.ok()
                .body(flux, BeerDTO.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request){
        return  ServerResponse.ok().body(
                beerService.getById(request.pathVariable("beerId")).switchIfEmpty(
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                ),BeerDTO.class
                );
    }

    public Mono<ServerResponse> createNewBeer(ServerRequest request){
        /*
         Reason of using flatMap in the place of map is that the ServerResponse.build() return a mono<ServerResponse> and
         map will also return a mono<> so if we use map the return type will be Mono<Mono<<ServerResponse>>> and to flatten
         the second mono we are using flatMap.
         */
        return beerService.saveBeer(request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate))
                .flatMap((beerDTO) -> ServerResponse.created(
                UriComponentsBuilder.fromPath(
                        BeerRouterConfig.BEER_PATH_ID
                ).build(beerDTO.getId())).build()
        );
    }
    public Mono<ServerResponse> updateBeerById(ServerRequest request){

       return  request.bodyToMono(BeerDTO.class)
               .doOnNext(this::validate).
               flatMap(beerDTO ->
                       beerService.updateBeer(request.pathVariable("beerId"),beerDTO)
                               .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))))
               .flatMap(beerDTO1 -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> patchBeerById(ServerRequest request){
        return request.bodyToMono(BeerDTO.class)
                .doOnNext(this::validate)
                .flatMap(
                beerDTO -> beerService.patchBeer(request.pathVariable("beerId"),beerDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))))
                .flatMap(beerDTO -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteBeerById(ServerRequest request){
        return beerService.getById(request.pathVariable("beerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beerDTO -> beerService.deleteBeerById(beerDTO.getId()))
                .then(ServerResponse.noContent().build());

    }
}
