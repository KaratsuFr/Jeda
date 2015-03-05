package fr.ippon.tlse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.Domain2ResourceMapper;
import fr.ippon.tlse.dto.utils.Resource2DomainMapper;

public class MockTuBasicBuniess implements IBusinessService<TuBasicDomain> {

	private Map<String, TuBasicDomain>	mockPersistance	= new HashMap<>();

	public MockTuBasicBuniess() {
		for (int i = 0; i < 10; i++) {
			mockPersistance.put("" + i, TuBasicDomain.builder().num(i).text(UUID.randomUUID().toString()).build());
		}
	}

	@Override
	public ResourceDto readAll(Class<TuBasicDomain> domainClass) {
		ApplicationUtils.SINGLETON.resetCacheClass();
		Domain2ResourceMapper.SINGLETON.resetCache();
		return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(
				new ArrayList<TuBasicDomain>(mockPersistance.values()), domainClass);
	}

	@Override
	public ResourceDto searchByCriteria(ResourceDto resource, Class<TuBasicDomain> domainClass) {
		return null;
	}

	@Override
	public ResourceDto readById(String id, Class<TuBasicDomain> domainClass) {
		TuBasicDomain dom = mockPersistance.get(id);

		ArrayList<TuBasicDomain> lstResult = new ArrayList<>();
		lstResult.add(dom);
		return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstResult, domainClass);
	}

	@Override
	public ResourceDto createOrUpdate(ResourceDto resource, Class<TuBasicDomain> domainClass) {
		List<TuBasicDomain> lstDomain = Resource2DomainMapper.SINGLETON.buildLstDomainFromResource(resource,
				TuBasicDomain.class);
		for (TuBasicDomain tuBasicDomain : lstDomain) {
			if (tuBasicDomain.getNum() == null) {
				tuBasicDomain.setNum(mockPersistance.size());
			}
			mockPersistance.put("" + tuBasicDomain.getNum(), tuBasicDomain);

		}
		return resource;
	}

	@Override
	public boolean deleteById(String id, Class<TuBasicDomain> domainClass) {
		return mockPersistance.remove(id) != null;

	}

}
