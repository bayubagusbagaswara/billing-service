package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;

public abstract class BaseMapper<E, D> {
    protected final ModelMapper modelMapper;

    protected BaseMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureMapper();
    }

    private void configureMapper() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.addMappings(getPropertyMap());
    }

    protected abstract PropertyMap<E, D> getPropertyMap();

    public E mapToEntity(D dto) {
        return modelMapper.map(dto, getEntityClass());
    }

    public D mapToDto(E entity) {
        return modelMapper.map(entity, getDtoClass());
    }

    public List<D> mapToDTOList(List<E> entityList) {
        return entityList.stream()
                .map(this::mapToDto)
                .toList();
    }

    public D mapFromCreateRequestToDto(Object createRequest, Class<D> dtoClass) {
        return modelMapper.map(createRequest, dtoClass);
    }

    public D mapFromUpdateRequestToDto(Object updateRequest, Class<D> dtoClass) {
        return modelMapper.map(updateRequest, dtoClass);
    }


    public E createEntity(D dto, BillingDataChangeDTO dataChangeDTO) {
        E entity = mapToEntity(dto);
        setCommonProperties(entity, dataChangeDTO);
        return entity;
    }

    public E updateEntity(E updatedEntity, Class<D> dto, BillingDataChangeDTO dataChangeDTO) {
        E entity = mapToEntity(mapToDto(updatedEntity));
        setCommonProperties(entity, dataChangeDTO);
        return entity;
    }

    public void mapObjects(D sourceDto, E targetEntity) {
        modelMapper.map(sourceDto, targetEntity);
    }

    protected abstract Class<E> getEntityClass();

    protected abstract Class<D> getDtoClass();

    protected abstract void setCommonProperties(E entity, BillingDataChangeDTO dataChangeDTO);

}
