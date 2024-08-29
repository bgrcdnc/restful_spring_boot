package com.bugracdnc.restmvc.mappers;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
