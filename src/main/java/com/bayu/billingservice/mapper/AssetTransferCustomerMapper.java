package com.bayu.billingservice.mapper;

import com.bayu.billingservice.dto.assettransfercustomer.AssetTransferCustomerDTO;
import com.bayu.billingservice.dto.assettransfercustomer.CreateAssetTransferCustomerRequest;
import com.bayu.billingservice.dto.assettransfercustomer.UpdateAssetTransferCustomerListRequest;
import com.bayu.billingservice.dto.datachange.BillingDataChangeDTO;
import com.bayu.billingservice.model.AssetTransferCustomer;
import com.bayu.billingservice.model.enumerator.ApprovalStatus;
import com.bayu.billingservice.util.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssetTransferCustomerMapper {

    private final ModelMapperUtil modelMapperUtil;
    private final ConvertDateUtil convertDateUtil;

    public AssetTransferCustomer mapFromDtoToEntity(AssetTransferCustomerDTO assetTransferCustomerDTO) {
        AssetTransferCustomer assetTransferCustomer = new AssetTransferCustomer();
        modelMapperUtil.mapObjects(assetTransferCustomerDTO, assetTransferCustomer);
        return assetTransferCustomer;
    }

    public  AssetTransferCustomerDTO mapFromEntityToDto( AssetTransferCustomer assetTransferCustomer) {
        ModelMapper modelMapper = modelMapperUtil.getModelMapper();
        modelMapper.addMappings(new PropertyMap<AssetTransferCustomer,  AssetTransferCustomerDTO>() {
            @Override
            protected void configure() {
                skip(destination.getApprovalStatus());
                skip(destination.getInputId());
                skip(destination.getInputIPAddress());
                skip(destination.getInputDate());
                skip(destination.getApproveId());
                skip(destination.getApproveIPAddress());
                skip(destination.getApproveDate());
            }
        });

        return modelMapper.map(assetTransferCustomer, AssetTransferCustomerDTO.class);
    }

    public List<AssetTransferCustomerDTO> mapToDTOList(List<AssetTransferCustomer> assetTransferCustomerList) {
        return assetTransferCustomerList.stream()
                .map(this::mapFromEntityToDto)
                .toList();
    }

    public AssetTransferCustomerDTO mapFromCreateCustomerRequestToDTO(CreateAssetTransferCustomerRequest createAssetTransferCustomerRequest) {
        AssetTransferCustomerDTO assetTransferCustomerDTO = new  AssetTransferCustomerDTO();
        modelMapperUtil.mapObjects(createAssetTransferCustomerRequest, assetTransferCustomerDTO);
        return assetTransferCustomerDTO;
    }

    public AssetTransferCustomer createEntity(AssetTransferCustomerDTO assetTransferCustomerDTO, BillingDataChangeDTO dataChangeDTO) {
        AssetTransferCustomer assetTransferCustomer = new  AssetTransferCustomer();
        modelMapperUtil.mapObjects(assetTransferCustomerDTO, assetTransferCustomer);
        assetTransferCustomer.setApprovalStatus(ApprovalStatus.APPROVED);
        assetTransferCustomer.setInputId(dataChangeDTO.getInputId());
        assetTransferCustomer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        assetTransferCustomer.setInputDate(dataChangeDTO.getInputDate());
        assetTransferCustomer.setApproveId(dataChangeDTO.getApproveId());
        assetTransferCustomer.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        assetTransferCustomer.setApproveDate(convertDateUtil.getDate());
        return assetTransferCustomer;
    }

    public AssetTransferCustomer updateEntity(AssetTransferCustomer assetTransferCustomerUpdated, BillingDataChangeDTO dataChangeDTO) {
        AssetTransferCustomer assetTransferCustomer = new AssetTransferCustomer();
        modelMapperUtil.mapObjects(assetTransferCustomerUpdated, assetTransferCustomer);
        assetTransferCustomer.setApprovalStatus(dataChangeDTO.getApprovalStatus());
        assetTransferCustomer.setInputId(dataChangeDTO.getInputId());
        assetTransferCustomer.setInputIPAddress(dataChangeDTO.getInputIPAddress());
        assetTransferCustomer.setInputDate(dataChangeDTO.getInputDate());
        assetTransferCustomer.setApproveId(dataChangeDTO.getApproveId());
        assetTransferCustomer.setApproveIPAddress(dataChangeDTO.getApproveIPAddress());
        assetTransferCustomer.setApproveDate(convertDateUtil.getDate());
        return assetTransferCustomer;
    }

    public AssetTransferCustomerDTO mapFromUpdateRequestToDto(UpdateAssetTransferCustomerListRequest updateAssetTransferCustomerListRequest) {
        AssetTransferCustomerDTO assetTransferCustomerDTO = new  AssetTransferCustomerDTO();
        modelMapperUtil.mapObjects(updateAssetTransferCustomerListRequest, assetTransferCustomerDTO);
        return assetTransferCustomerDTO;
    }

    public void mapObjects(AssetTransferCustomerDTO assetTransferCustomerDTOSource, AssetTransferCustomer assetTransferCustomerTarget) {
        modelMapperUtil.mapObjects(assetTransferCustomerDTOSource, assetTransferCustomerTarget);
    }

}
