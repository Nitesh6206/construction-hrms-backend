package com.example.demo.site;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Site getSiteById(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found with id: " + id));
    }

    public Site createSite(Site site) {
        return siteRepository.save(site);
    }

    public Site updateSite(Long id, Site siteDetails) {
        Site existing = getSiteById(id);
        existing.setName(siteDetails.getName());
        existing.setLocation(siteDetails.getLocation());
        existing.setActive(siteDetails.isActive());
        return siteRepository.save(existing);
    }

    public void deleteSite(Long id) {
        Site site = getSiteById(id);
        site.setActive(false); // Soft delete
        siteRepository.save(site);
    }
}