export interface Event {
    id: string; 
    glpiTicketId: string;
    title: string;
    description: string;
    requestingDepartment: string;
    responsibleName: string;
    responsibleEmail: string;
    responsiblePhone: string | null;
    startDateTime: string;
    endDateTime: string;
    physicalSpace: string; 
    eventType: string;
    estimatedAttendees: number | null;
    equipmentNeeded: string[];
    needsEarlySetup: boolean; 
    needsTechnicalAssistance: boolean; 
    recurring: boolean;
    dateMod: string; 
}