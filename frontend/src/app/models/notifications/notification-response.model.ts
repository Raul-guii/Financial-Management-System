import { NotificationType } from "./notification-type.enum.model";

export interface NotificationResponse {
  id: number;

  title: string;

  message: string;

  type: NotificationType;

  isRead: boolean;

  userId: number;

  createdAt: string;
}