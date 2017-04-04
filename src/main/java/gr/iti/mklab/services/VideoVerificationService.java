package gr.iti.mklab.services;

/**
 * Video verification service
 * @author olgapapa
 *
 */

public interface VideoVerificationService {
	void createReport(String id, String fields, boolean videoExist);
}
