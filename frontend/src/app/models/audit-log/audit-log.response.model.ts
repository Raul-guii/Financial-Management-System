export interface AuditLogResponse {
  id: number;
  entityId: number;
  entityType: string;
  action: string;
  userName: string | null;
  description: string | null;
  created_at: string;
  userId: number | null;
}