package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
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

    public D mapFromCreateRequestToDto(Object createRequest) {
        return modelMapper.map(createRequest, getDtoClass());
    }

    public D mapFromUpdateRequestToDto(Object updateRequest) {
        return modelMapper.map(updateRequest, getDtoClass());
    }


    public E createEntity(D dto, BillingDataChangeDTO dataChangeDTO) {
        E entity = mapToEntity(dto);
        setCommonProperties(entity, dataChangeDTO);
        return entity;
    }

    public E updateEntity(E updatedEntity, BillingDataChangeDTO dataChangeDTO) {
        E entity = mapToEntity(mapToDto(updatedEntity));
        setCommonProperties(entity, dataChangeDTO);
        return entity;
    }

    public void mapObjectsDtoToEntity(D sourceDto, E targetEntity) {
        modelMapper.map(sourceDto, targetEntity);
    }

    public void mapObjectsEntityToDto(E sourceEntity, D targetDto) {
        modelMapper.map(sourceEntity, targetDto);
    }

    protected abstract Class<E> getEntityClass();

    protected abstract Class<D> getDtoClass();

    protected abstract void setCommonProperties(E entity, BillingDataChangeDTO dataChangeDTO);

    public D mapFromDataListToDTO(Object listRequest) {
        Class<D> dtoClass = getDtoClass();
        D dto = modelMapper.map(listRequest, dtoClass);

        try {
            Method[] methods = dtoClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String methodName = method.getName().substring(3);
                    if (methodName.equalsIgnoreCase("id")) continue;
                    Object value = method.invoke(dto);
                    if (value == null) {
                        String setterName = "set" + methodName;
                        log.info("Setter Name: {}", setterName);
                        Method setter = dtoClass.getDeclaredMethod(setterName, method.getReturnType());
                        setter.invoke(dto, "");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while processing map from data list to dto: {}", e.getMessage(), e);
        }

        return dto;
    }

}
