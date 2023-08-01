package com.amiya.reactivemongo.mapper;

import com.amiya.reactivemongo.domain.Beer;
import com.amiya.reactivemongo.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    BeerDTO beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDTO beerDTO);
}