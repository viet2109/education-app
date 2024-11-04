// components/MediaManagerModal.tsx
import React, { useState, useEffect } from "react";
import { Media } from "../types";
import Modal from "./modal";

interface MediaManagerModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (media: Media) => void;
}

const MediaManagerModal: React.FC<MediaManagerModalProps> = ({
  isOpen,
  onClose,
  onSelect,
}) => {
  const [mediaList, setMediaList] = useState<Media[]>([]);
  const [selectedMedia, setSelectedMedia] = useState<Media | null>(null);

  // Fetch media files from a source
  const fetchMediaList = async () => {
    const mediaData: Media[] = [
      {
        id: 1,
        filename: "example.jpg",
        fileUrl: "http://example.com/example.jpg",
        fileType: "image/jpeg",
        sizeInBytes: 204800,
        createdDate: "2023-11-01T12:30:00",
        updatedDate: "2023-11-01T12:30:00",
      },
      // Thêm các đối tượng Media khác ở đây
    ];
    setMediaList(mediaData);
  };

  useEffect(() => {
    if (isOpen) {
      fetchMediaList();
    }
  }, [isOpen]);

  const handleSelectMedia = (media: Media) => {
    setSelectedMedia(media);
  };

  const handleCloseDetails = () => {
    setSelectedMedia(null);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div>
        <h2 className="text-xl font-bold mb-4">Manage Media Files</h2>

        <div className="overflow-x-auto mb-4">
          <table className="min-w-full bg-white border border-gray-300 rounded-lg shadow-sm">
            <thead>
              <tr className="bg-gray-100 border-b">
                <th className="px-4 py-2 text-left *:line-clamp-1">
                  <span>Filename</span>
                </th>
                <th className="px-4 py-2 text-left *:line-clamp-1">
                  <span>File Type</span>
                </th>
                <th className="px-4 py-2 text-left *:line-clamp-1">
                  <span>Size (Bytes)</span>
                </th>
                <th className="px-4 py-2 text-left *:line-clamp-1">
                  <span>Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {mediaList.map((media) => (
                <tr key={media.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-2 *:line-clamp-1">
                    <span>{media.filename}</span>
                  </td>
                  <td className="px-4 py-2 *:line-clamp-1">
                    <span>{media.fileType}</span>
                  </td>
                  <td className="px-4 py-2 *:line-clamp-1">
                    <span>{media.sizeInBytes}</span>
                  </td>
                  <td className="px-4 py-2 *:line-clamp-1">
                    <button
                      onClick={() => handleSelectMedia(media)}
                      className="text-blue-500 hover:underline mr-2"
                    >
                      View
                    </button>
                    <button
                      onClick={() => onSelect(media)}
                      className="text-green-500 hover:underline"
                    >
                      Select
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {selectedMedia && (
          <div className="bg-gray-50 p-4 rounded-lg shadow-inner mb-4">
            <h3 className="text-lg font-semibold mb-2">Media Details</h3>
            <p>
              <strong>Filename:</strong> {selectedMedia.filename}
            </p>
            <p>
              <strong>File URL:</strong>{" "}
              <a
                href={selectedMedia.fileUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="text-blue-500 hover:underline"
              >
                {selectedMedia.fileUrl}
              </a>
            </p>
            <p>
              <strong>File Type:</strong> {selectedMedia.fileType}
            </p>
            <p>
              <strong>Size (Bytes):</strong> {selectedMedia.sizeInBytes}
            </p>
            <p>
              <strong>Created Date:</strong> {selectedMedia.createdDate}
            </p>
            <p>
              <strong>Updated Date:</strong> {selectedMedia.updatedDate}
            </p>
            <button
              onClick={handleCloseDetails}
              className="mt-2 text-sm text-red-500 hover:underline"
            >
              Close Details
            </button>
          </div>
        )}

        <button
          onClick={onClose}
          className="mt-4 w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg"
        >
          Close
        </button>
      </div>
    </Modal>
  );
};

export default MediaManagerModal;
